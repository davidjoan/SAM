package pe.cayro.sam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.adapter.SpecialtyAutocompleterAdapter;
import pe.cayro.sam.model.Doctor;
import pe.cayro.sam.model.Specialty;
import util.Constants;

public class NewDoctorActivity extends AppCompatActivity {

    private static String TAG = NewDoctorActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.doctor_save)
    protected Button doctorSave;
    @Bind(R.id.doctor_code)
    protected EditText doctorCode;
    @Bind(R.id.doctor_name)
    protected EditText doctorName;
    @Bind(R.id.doctor_specialty_autocompleter)
    protected AppCompatAutoCompleteTextView doctorSpecialty;

    private Realm realm;
    private Doctor doctor;
    private Specialty specialty;
    private SpecialtyAutocompleterAdapter adapterSpecialty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_doctor);

        ButterKnife.bind(this);
        toolbar.setTitle(R.string.title_activity_new_doctor);

        realm = Realm.getDefaultInstance();

        setSupportActionBar(toolbar);

        doctor = new Doctor();

        adapterSpecialty = new SpecialtyAutocompleterAdapter(this,
                R.layout.specialty_autocomplete_item);
        doctorSpecialty.setAdapter(adapterSpecialty);
        doctorSpecialty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer temp = adapterSpecialty.getItem(position);

                specialty = realm.where(Specialty.class).equalTo(Constants.ID,
                        temp.intValue()).findFirst();
                doctorSpecialty.setText(specialty.getName());

                doctor.setSpecialty(specialty);
                doctor.setSpecialtyId(specialty.getId());
            }
        });

        doctorSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int countErrors = 0;

                if (doctorCode.getText().length() < 2) {
                    countErrors++;
                    doctorCode.setError("El Código CMP es requerido.");
                }
                if (doctorName.getText().length() < 2) {
                    countErrors++;
                    doctorName.setError("El Nombre y Apellido es requerido.");
                }
                if (doctorSpecialty.getText().length() < 2) {
                    countErrors++;
                    doctorSpecialty.setError("La Especialidad es requerida.");
                }

                if (countErrors == 0) {

                    try {

                        realm.beginTransaction();

                        doctor.setUuid(UUID.randomUUID().toString());
                        doctor.setCode(doctorCode.getText().toString());
                        doctor.setName(doctorName.getText().toString());
                        doctor.setActive(true);
                        doctor.setScore("X");

                        realm.copyToRealmOrUpdate(doctor);
                        realm.commitTransaction();

                        Toast.makeText(getApplicationContext(), Constants.SAVE_OK,
                                Toast.LENGTH_SHORT).show();

                        Intent data = new Intent();
                        data.putExtra(Constants.UUID, doctor.getUuid());

                        if (getParent() == null) {
                            setResult(Activity.RESULT_OK, data);
                        } else {
                            getParent().setResult(Activity.RESULT_OK, data);
                        }
                        finish();

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    } finally {
                        realm.close();
                    }
                }
            }
        });
    }
}
