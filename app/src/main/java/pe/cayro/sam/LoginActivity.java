package pe.cayro.sam;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.model.Institution;
import pe.cayro.sam.model.Tracking;
import pe.cayro.sam.model.User;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    String institutionName;

    @Bind(R.id.buttonLogin)
    Button loginButton;

    @Bind(R.id.text_second)
    TextView institutionTextView;

    @Bind(R.id.editTextAtentionCode)
    EditText atentionCodeEditText;


    Institution institution;
    User user;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buildGoogleApiClient();

        institutionName = getIntent().getStringExtra("institution_name");

        ButterKnife.bind(this);

        realm = Realm.getInstance(getApplicationContext());

        institution = realm.where(Institution.class).equalTo("name",institutionName).findFirst();

        user = realm.where(User.class).findFirst();

        toolbar.setTitle("Iniciar Sesión");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        institutionTextView.setText(institutionName);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(atentionCodeEditText.getText().length() > 0){

                    //int nextID = (int) (realm.where(Tracking.class).max("id").intValue() + 1);

                    Toast.makeText(LoginActivity.this, atentionCodeEditText.getText().toString(), Toast.LENGTH_SHORT).show();

                    realm.beginTransaction();
                    Tracking tracking = new Tracking();
                    tracking.setUuid(UUID.randomUUID().toString());
                    tracking.setCode(atentionCodeEditText.getText().toString());
                    tracking.setType("login");
                    tracking.setInstitutionId(institution.getId());
                    tracking.setCreatedAt(new Date());
                    tracking.setUserId(user.getId()); //TODO: add real user ID
                    tracking.setInstitution(institution);

                    if(mLastLocation != null){
                        tracking.setLatitude(mLastLocation.getLatitude());
                        tracking.setLongitude(mLastLocation.getLongitude());
                    }

                    realm.copyToRealm(tracking);

                    realm.commitTransaction();

                    Intent intent = new Intent(LoginActivity.this, InstitutionActivity.class);
                    intent.putExtra("tracking_code", tracking.getCode());
                    intent.putExtra("institution_name", institutionName);


                    LoginActivity.this.startActivity(intent);
                    finish();

                }else {

                     Toast.makeText(LoginActivity.this, "Ingrese el Número de Atención", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            Log.i(TAG, "latitude:" + String.valueOf(mLastLocation.getLatitude()));
            Log.i(TAG, "longitude" + String.valueOf(mLastLocation.getLongitude()));
        } else {
            Log.i(TAG,"No esta activado el gps.");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended, code:" + String.valueOf(i));
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
}
