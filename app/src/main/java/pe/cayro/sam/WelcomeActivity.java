package pe.cayro.sam;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.api.RestClient;
import pe.cayro.sam.model.Agent;
import pe.cayro.sam.model.AttentionType;
import pe.cayro.sam.model.Doctor;
import pe.cayro.sam.model.Institution;
import pe.cayro.sam.model.Product;
import pe.cayro.sam.model.Specialty;
import pe.cayro.sam.model.Ubigeo;
import pe.cayro.sam.model.User;
import util.Constants;

public class WelcomeActivity extends AppCompatActivity {
    private static String TAG = WelcomeActivity.class.getSimpleName();
    private static final int SPLASH_DURATION = 1500;

    @Bind(R.id.logo_sam)
    protected ImageView logo;
    private Handler handler;
    private ProgressDialog progress;
    private boolean mIsBackButtonPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.bind(this);

        progress = new ProgressDialog(this);

        Snackbar.make(logo, R.string.loading_app, Snackbar.LENGTH_LONG)
                .setAction(Constants.ACTION, null)
                .show();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_SAM, 0);
                String cycleLoaded = settings.getString(Constants.CYCLE_LOADED, "");

                if (cycleLoaded.equals(Constants.YES)) {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    WelcomeActivity.this.startActivity(intent);
                    finish();
                } else {
                    progress.setCancelable(false);
                    progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progress.setMax(9);
                    progress.setMessage(Constants.SINCRONIZATION);
                    progress.show();
                    new LoginAsyncTask(getApplicationContext()).execute(Constants.EMPTY);
                }
            }
        }, SPLASH_DURATION);
    }

    @Override
    public void onBackPressed() {
        mIsBackButtonPressed = true;
        super.onBackPressed();
    }

    public class LoginAsyncTask extends AsyncTask<String, String, Integer> {

        Context context;
        private Handler handler;

        public LoginAsyncTask(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            handler = new Handler();
            progress.setProgress(0);
        }

        protected void onProgressUpdate(String...a)
        {
            super.onProgressUpdate(a);

            if (a != null && a.length > 0) {
                final String msg = a[0];
                handler.post(new Runnable() {
                    public void run() {
                        progress.setProgress(progress.getProgress()+1);
                        progress.setMessage(msg);
                    }
                });
            }
        }

        @Override
        public Integer doInBackground(String... params) {
            int result = 0;

            SharedPreferences settings = context.
                    getSharedPreferences(Constants.PREFERENCES_SAM, 0);
            SharedPreferences.Editor editor = settings.edit();
            TelephonyManager telephonyManager = (TelephonyManager) context.
                    getSystemService(Context.TELEPHONY_SERVICE);


            this.publishProgress(Constants.OBTAINING_IMEI);

            /*
            TODO: 17/07/15
            Replace for implement in production enable the following lines of code
            Log.i(TAG, telephonyManager.getDeviceId());
            String imei = telephonyManager.getDeviceId();
            */
            String imei = Constants.IMEI_TEST;

            Realm realm = Realm.getDefaultInstance();

            try{

                realm.beginTransaction();
                realm.clear(Institution.class);

                realm.clear(AttentionType.class);
                realm.clear(User.class);
                realm.clear(Specialty.class);
                realm.clear(Product.class);
                realm.clear(Doctor.class);
                realm.clear(Agent.class);
                realm.clear(Ubigeo.class);

                this.publishProgress(Constants.LOADING_USERS);
                User user = RestClient.get().getUserByImei(imei);
                realm.copyToRealmOrUpdate(user);

                this.publishProgress(Constants.LOADING_INSTITUTIONS);
                List<Institution> institutions = RestClient.get().getListInstitutions();
                realm.copyToRealmOrUpdate(institutions);

                this.publishProgress(Constants.LOADING_ATTENTION_TYPES);
                List<AttentionType> attentionTypes = RestClient.get().getAttentionTypes();
                realm.copyToRealmOrUpdate(attentionTypes);

                this.publishProgress(Constants.LOADING_SPECIALTIES);
                List<Specialty> specialties = RestClient.get().getListSpecialties();
                realm.copyToRealmOrUpdate(specialties);

                this.publishProgress(Constants.LOADING_DOCTORS);
                List<Doctor> doctors = RestClient.get().getListDoctors();

                this.publishProgress(Constants.LOADING_UBIGEOS);
                List<Ubigeo> ubigeos = RestClient.get().getUbigeos();
                realm.copyToRealmOrUpdate(ubigeos);

                List<Doctor> doctorsTemp = new ArrayList<Doctor>();
                for(Doctor temp : doctors){
                    Specialty tempEsp = realm.where(Specialty.class).equalTo(Constants.ID,
                            temp.getSpecialtyId()).findFirst();
                    temp.setSpecialty(tempEsp);
                    doctorsTemp.add(temp);
                }

                realm.copyToRealmOrUpdate(doctorsTemp);

                this.publishProgress(Constants.LOADING_PRODUCTS);
                List<Product> products = RestClient.get().getListProducts();
                realm.copyToRealmOrUpdate(products);

                this.publishProgress(Constants.LOADING_AGENTS);
                List<Agent> agents = RestClient.get().getAgents();
                realm.copyToRealmOrUpdate(agents);
                realm.commitTransaction();

                editor.putString(Constants.CYCLE_LOADED, Constants.YES);
                editor.putString(Constants.SNACK,Constants.NO);
                editor.putString(Constants.SESSION,Constants.NO);
                editor.apply();

            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            progress.dismiss();

            switch (result) {
                case 0:
                    Snackbar.make(logo, R.string.login_success, Snackbar.LENGTH_LONG)
                            .setAction(Constants.ACTION, null)
                            .show();
                    break;
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mIsBackButtonPressed) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        WelcomeActivity.this.startActivity(intent);
                        finish();
                    }
                }
            }, SPLASH_DURATION);
        }
    }
}
