package pe.cayro.sam;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.model.Patient;
import pe.cayro.sam.model.User;

public class NewPatientActivity extends AppCompatActivity {

    private static String TAG = NewPatientActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.patient_save)
    protected Button patientSave;

    @Bind(R.id.patient_code)
    protected EditText patientCode;

    @Bind(R.id.patient_name)
    protected EditText patientName;

    @Bind(R.id.patient_phone)
    protected EditText patientPhone;

    @Bind(R.id.patient_address)
    protected EditText patientAddress;

    @Bind(R.id.patient_location)
    protected EditText patientLocation;

    @Bind(R.id.patient_mail)
    protected EditText patientEmail;

    Realm realm;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        ButterKnife.bind(this);

        toolbar.setTitle("Nuevo Paciente");

        realm = Realm.getDefaultInstance();

        user =realm.where(User.class).findFirst();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        patientSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int countErrors = 0;

                if(patientPhone.getText().length() < 2){
                    countErrors++;
                    patientPhone.setError("El teléfono es requerido.");
                }
                if(patientName.getText().length() < 2){
                    countErrors++;
                    patientName.setError("El Nombre y Apellido es requerido.");
                }
                if(patientCode.getText().length() != 8){
                    countErrors++;
                    patientCode.setError("El DNI debe tener 8 digitos");
                }

                if(countErrors == 0){

                    try {

                        realm.beginTransaction();

                        Patient patient = realm.createObject(Patient.class);

                        patient.setUuid(UUID.randomUUID().toString());
                        patient.setCode(patientCode.getText().toString());
                        patient.setName(patientName.getText().toString());
                        patient.setAddress(patientAddress.getText().toString());
                        patient.setLocation(patientLocation.getText().toString());
                        patient.setPhone(patientPhone.getText().toString());
                        patient.setEmail(patientEmail.getText().toString());
                        patient.setCreatedAt(new Date());
                        patient.setUser(user);
                        patient.setUserId(user.getId());

                        realm.copyToRealmOrUpdate(patient);

                        realm.commitTransaction();

                        Toast.makeText(getApplicationContext(), "Se guardo correctamente.",
                                Toast.LENGTH_SHORT).show();

                        Intent data = new Intent();

                        if (getParent() == null) {
                            setResult(Activity.RESULT_OK, data);
                        } else {
                            getParent().setResult(Activity.RESULT_OK, data);
                        }
                        finish();

                    }catch (Exception e){
                        Log.e(TAG, e.toString());
                    }finally {
                        realm.close();
                    }
                }
            }
        });
    }
}
