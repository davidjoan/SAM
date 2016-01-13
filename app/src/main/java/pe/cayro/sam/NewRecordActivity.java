package pe.cayro.sam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.adapter.DoctorAutocompleterAdapter;
import pe.cayro.sam.model.Doctor;
import pe.cayro.sam.model.Record;
import pe.cayro.sam.model.Tracking;
import util.Constants;

public class NewRecordActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,  AdapterView.OnItemClickListener{

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.record_attention_type_spinner)
    protected Spinner spinner;

    @Bind(R.id.record_code)
    protected EditText recordCode;

    @Bind(R.id.record_date)
    protected EditText editTextDate;

    @Bind(R.id.record_date_button)
    protected ImageButton imageButtonDate;

    @Bind(R.id.record_doctor_autocompleter)
    protected AppCompatAutoCompleteTextView recordDoctor;

    String trackingUuid;

    Realm realm;

    Tracking tracking;

    Record record;

    private Calendar start;

    private SimpleDateFormat format;
    private SimpleDateFormat sdf;

    protected DoctorAutocompleterAdapter adapterAuto;
    private Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        format = new SimpleDateFormat(Constants.FORMAT_DATE_SLASH);
        sdf = new SimpleDateFormat(Constants.FORMAT_DATE);

        ButterKnife.bind(this);

        start = Calendar.getInstance();

        realm = Realm.getDefaultInstance();

        trackingUuid = getIntent().getStringExtra("tracking_uuid");

        tracking = realm.where(Tracking.class).equalTo("uuid", trackingUuid).findFirst();

        toolbar.setTitle("Nuevo Registro de MM");
        toolbar.setSubtitle(tracking.getInstitution().getName());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        record = new Record();

        record.setUuid(UUID.randomUUID().toString());
        record.setInstitutionId(tracking.getInstitutionId());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.attention_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        recordCode.setText(tracking.getCode());

        editTextDate.setText(sdf.format(new Date()));

        final DatePickerDialog datePickerDialog = DatePickerDialog.
                newInstance(this, start.get(Calendar.YEAR), start.get(Calendar.MONTH),
                        start.get(Calendar.DAY_OF_MONTH), true);

        imageButtonDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(start.get(Calendar.YEAR),
                        start.get(Calendar.YEAR) + 1);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), Constants.DATEPICKER_TAG);
            }
        });

        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().
                    findFragmentByTag(Constants.DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }

        adapterAuto = new DoctorAutocompleterAdapter(this, R.layout.doctor_autocomplete_item);
        recordDoctor.setAdapter(adapterAuto);
        recordDoctor.setOnItemClickListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        start.set(year, month, day);
        String formatedDate = sdf.format(start.getTime());
        editTextDate.setText(formatedDate);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Integer temp = ((Integer) adapterAuto.getItem(position));

        doctor = realm.where(Doctor.class).equalTo("id",temp.intValue()).findFirst();
        recordDoctor.setText(doctor.getName());
    }
}
