package pe.cayro.sam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.sam.adapter.DoctorAutocompleterAdapter;
import pe.cayro.sam.adapter.PatientAutocompleterAdapter;
import pe.cayro.sam.model.AttentionType;
import pe.cayro.sam.model.Doctor;
import pe.cayro.sam.model.Patient;
import pe.cayro.sam.model.Record;
import pe.cayro.sam.model.Tracking;
import pe.cayro.sam.model.User;
import util.Constants;

public class NewRecordActivity extends AppCompatActivity {

    private static String TAG = NewRecordActivity.class.getSimpleName();

    static final int ADD_DOCTOR_REQUEST = 1;
    static final int ADD_PATIENT_REQUEST = 2;
    static final int ADD_MEDICAL_SAMPLE_REQUEST = 3;


    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.record_attention_type_spinner)
    protected Spinner spinner;

    @Bind(R.id.record_ruc)
    protected EditText recordRuc;
    @Bind(R.id.record_serial)
    protected EditText recordSerial;
    @Bind(R.id.record_voucher)
    protected EditText recordVoucher;
    @Bind(R.id.record_cancel_button)
    protected Button recordCancel;

    @Bind(R.id.record_code)
    protected EditText recordCode;
    @Bind(R.id.record_date)
    protected EditText editTextDate;
    @Bind(R.id.record_sale_date)
    protected EditText editTextSaleDate;
    @Bind(R.id.record_doctor_autocompleter)
    protected AppCompatAutoCompleteTextView recordDoctor;
    @Bind(R.id.record_patient_autocompleter)
    protected AppCompatAutoCompleteTextView recordPatient;

    private long code = 1;
    private User user;
    private Realm realm;
    private Record record;
    private Doctor doctor;
    private Calendar start;
    private Patient patient;
    private Tracking tracking;
    private String trackingUuid;
    private SimpleDateFormat sdf;
    private SimpleDateFormat format;
    private AttentionType attentionType;
    private DoctorAutocompleterAdapter adapterDoctor;
    private PatientAutocompleterAdapter adapterPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        trackingUuid = getIntent().getStringExtra(Constants.TRACKING_UUID);

        format = new SimpleDateFormat(Constants.FORMAT_DATE_SLASH);
        sdf    = new SimpleDateFormat(Constants.FORMAT_DATE);

        ButterKnife.bind(this);

        start = Calendar.getInstance();
        realm = Realm.getDefaultInstance();

        tracking = realm.where(Tracking.class).equalTo(Constants.UUID, trackingUuid).findFirst();

        RealmResults<Record> results = realm.where(Record.class).findAll();
        if(results.size() > 0 ){
            code = results.max(Constants.CODE).longValue()+1;

            if((long)tracking.getCode() > code){
                code = (long)tracking.getCode();
            }
        }else{
            code = (long)tracking.getCode();
        }

        toolbar.setTitle(R.string.new_record);
        toolbar.setSubtitle(tracking.getInstitution().getName());
        setSupportActionBar(toolbar);

        //Preparing Object
        record = new Record();
        record.setUuid(UUID.randomUUID().toString());
        record.setInstitutionId(tracking.getInstitutionId());
        record.setRecordDate(new Date());
        record.setSaleDate(new Date());
        record.setCode((int) code);

        attentionType = realm.where(AttentionType.class).equalTo(Constants.ID,
                2).findFirst();

        record.setAttentionTypeId(attentionType.getId());
        record.setAttentionType(attentionType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.attention_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d(TAG, "Tipo de Atención: " + String.valueOf(i));

                attentionType = realm.where(AttentionType.class).equalTo(Constants.ID,
                        i + 1).findFirst();

                record.setAttentionType(attentionType);
                record.setAttentionTypeId(attentionType.getId());

                switch (i) {
                    case 0:
                        findViewById(R.id.header_finish_treatment).setVisibility(View.GONE);
                        findViewById(R.id.body_finish_treatment_1).setVisibility(View.GONE);
                        findViewById(R.id.body_finish_treatment_2).setVisibility(View.GONE);
                        findViewById(R.id.body_patient).setVisibility(View.VISIBLE);

                        break;
                    case 1:
                        findViewById(R.id.header_finish_treatment).setVisibility(View.VISIBLE);
                        findViewById(R.id.body_finish_treatment_1).setVisibility(View.VISIBLE);
                        findViewById(R.id.body_finish_treatment_2).setVisibility(View.VISIBLE);
                        findViewById(R.id.body_patient).setVisibility(View.VISIBLE);

                        break;
                    case 2:
                        findViewById(R.id.header_finish_treatment).setVisibility(View.GONE);
                        findViewById(R.id.body_finish_treatment_1).setVisibility(View.GONE);
                        findViewById(R.id.body_finish_treatment_2).setVisibility(View.GONE);
                        findViewById(R.id.body_patient).setVisibility(View.GONE);

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "Tipo de Atención: Nada Seleccionado");
            }
        });



        recordCode.setText(String.valueOf(code));
        editTextDate.setText(sdf.format(new Date()));
        editTextSaleDate.setText(sdf.format(new Date()));

        adapterDoctor = new DoctorAutocompleterAdapter(this, R.layout.doctor_autocomplete_item);
        recordDoctor.setAdapter(adapterDoctor);
        recordDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String temp = adapterDoctor.getItem(position);
                doctor = realm.where(Doctor.class).equalTo(Constants.UUID, temp).findFirst();
                recordDoctor.setText(new StringBuilder().append(doctor.getFirstname()).
                        append(Constants.SPACE).append(doctor.getLastname()).
                        append(Constants.SPACE).append(doctor.getSurname()).toString());
                record.setDoctor(doctor);
                record.setDoctorUuid(doctor.getUuid());
            }
        });

        adapterPatient = new PatientAutocompleterAdapter(this, R.layout.patient_autocomplete_item);
        recordPatient.setAdapter(adapterPatient);
        recordPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String temp = adapterPatient.getItem(position);
                patient = realm.where(Patient.class).equalTo(Constants.UUID, temp).findFirst();
                recordPatient.setText(new StringBuilder().append(patient.getFirstname()).
                        append(Constants.SPACE).append(patient.getLastname()).
                        append(Constants.SPACE).append(patient.getSurname()).toString());
                record.setPatient(patient);
                record.setPatientUuid(patient.getUuid());
            }
        });

        recordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    getParent().setResult(Activity.RESULT_OK, intent);
                }

                finish();
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), Constants.DATEPICKER_TAG);
    }
    public void showSaleDatePickerDialog(View v) {
        DialogFragment newFragment = new SaleDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), Constants.DATEPICKER_TAG);
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener
    {
        public DatePickerFragment() {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Log.d(TAG, String.valueOf(year));
            Log.d(TAG, String.valueOf(month));
            Log.d(TAG, String.valueOf(day));

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker datePicker = dialog.getDatePicker();

            long time = c.getTimeInMillis();
            datePicker.setMaxDate(time+1000);

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            start.set(year, month, day);
            String formatedDate = sdf.format(start.getTime());
            editTextDate.setText(formatedDate);
            record.setRecordDate(start.getTime());
        }
    }

    @SuppressLint("ValidFragment")
    public class SaleDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener
    {
        public SaleDatePickerFragment() {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Log.d(TAG, String.valueOf(year));
            Log.d(TAG, String.valueOf(month));
            Log.d(TAG, String.valueOf(day));

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker datePicker = dialog.getDatePicker();

            long time = c.getTimeInMillis();
            datePicker.setMaxDate(time+1000);

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            start.set(year, month, day);
            String formatedDate = sdf.format(start.getTime());
            editTextSaleDate.setText(formatedDate);
            record.setSaleDate(start.getTime());
        }
    }

    public void openNewDoctor(View v) {
        Intent intent = new Intent(this, NewDoctorActivity.class);
        startActivityForResult(intent, ADD_DOCTOR_REQUEST);
    }
    public void openNewPatient(View v) {
        Intent intent = new Intent(this, NewPatientActivity.class);
        startActivityForResult(intent, ADD_PATIENT_REQUEST) ;
    }

    public void nextButton(View v) {

        int errors = 0;
        if(recordCode.getText().length() == 0){
            errors++;
            recordCode.setError("El codigo no puede estar vacio");
        }

        switch (record.getAttentionTypeId()){
            case 1:
                if(recordDoctor.getText().length() == 0){
                    errors++;
                    recordDoctor.setError("El Médico no puede estar vacio");
                }
                if(doctor == null){
                    errors++;
                    recordDoctor.setError("Tiene que seleccionar un Médico");
                }

                if(recordPatient.getText().length() == 0){
                    errors++;
                    recordPatient.setError("El Paciente no puede estar vacio");
                }
                break;
            case 2:
                if(recordDoctor.getText().length() == 0){
                    errors++;
                    recordDoctor.setError("El Médico no puede estar vacio");
                }
                if(doctor == null){
                    errors++;
                    recordDoctor.setError("Tiene que seleccionar un Médico");
                }
                if(recordPatient.getText().length() == 0){
                    errors++;
                    recordPatient.setError("El Paciente no puede estar vacio");
                }
                if(patient == null){
                    errors++;
                    recordPatient.setError("Tiene que seleccionar un Paciente");
                }
                if(recordSerial.getText().length() == 0){
                    errors++;
                    recordSerial.setError("La serie no puede estar vacia");
                }
                if(recordVoucher.getText().length() == 0){
                    errors++;
                    recordVoucher.setError("El # de Comprobante no puede estar vacio");
                }

                if(editTextSaleDate.getText().length() == 0){
                    errors++;
                    editTextSaleDate.setError("La fecha de venta no puede estar vacia");
                }

                if(recordRuc.getText().length() != 11){
                    errors++;
                    recordRuc.setError("El RUC debe tener 11 digitos");
                }
                break;
            case 3:
                if(recordDoctor.getText().length() == 0){
                    errors++;
                    recordDoctor.setError("El Médico no puede estar vacio");
                }
                if(doctor == null){
                    errors++;
                    recordDoctor.setError("Tiene que seleccionar un Médico");
                }
                break;
        }

        if(errors == 0){

            user = realm.where(User.class).findFirst();

            realm.beginTransaction();

            record.setCode(Integer.valueOf(recordCode.getText().toString()));
            record.setAttentionTypeId(attentionType.getId());
            record.setAttentionType(attentionType);
            record.setInstitutionId(tracking.getInstitutionId());
            record.setInstitution(tracking.getInstitution());
            record.setInstitutionOriginId(tracking.getInstitutionId());
            record.setInstitutionOrigin(tracking.getInstitution());
            record.setUserId(tracking.getUserId());
            record.setUser(user);

            record.setCreatedAt(new Date());
            record.setUpdatedAt(new Date());

            //Finish treatment
            if(record.getAttentionTypeId() == 2){
                record.setVoucher(recordVoucher.getText().toString());
                record.setRuc(recordRuc.getText().toString());
                record.setSerial(recordSerial.getText().toString());
            }

            realm.copyToRealm(record);

            realm.commitTransaction();

            Intent intent = new Intent(this, AddRecordDetailActivity.class);
            intent.putExtra(Constants.UUID, record.getUuid());
            intent.putExtra(Constants.CODE, record.getCode());
            startActivityForResult(intent, ADD_MEDICAL_SAMPLE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PATIENT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                String uuid = data.getStringExtra(Constants.UUID);

                patient = realm.where(Patient.class).equalTo(Constants.UUID, uuid).findFirst();
                recordPatient.setText(new StringBuilder().append(patient.getFirstname()).
                        append(Constants.SPACE).append(patient.getLastname()).
                        append(Constants.SPACE).append(patient.getSurname()).toString());

                record.setPatient(patient);
                record.setPatientUuid(patient.getUuid());
            }
        }

        if (requestCode == ADD_DOCTOR_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String uuid = data.getStringExtra(Constants.UUID);
                doctor = realm.where(Doctor.class).equalTo(Constants.UUID, uuid).findFirst();
                recordDoctor.setText(new StringBuilder().append(doctor.getFirstname()).
                        append(Constants.SPACE).append(doctor.getLastname()).
                        append(Constants.SPACE).append(doctor.getSurname()).toString());
                record.setDoctor(doctor);
                record.setDoctorUuid(doctor.getUuid());
            }
        }

        if (requestCode == ADD_MEDICAL_SAMPLE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    getParent().setResult(Activity.RESULT_OK, intent);
                }

                finish();
            }
        }
    }

    /**
     * Exit the app if user select yes.
     */
    private void doExit() {
    }

    @Override
    public void onBackPressed() {
        doExit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            doExit();
        }
        return super.onKeyDown(keyCode, event);
    }
}