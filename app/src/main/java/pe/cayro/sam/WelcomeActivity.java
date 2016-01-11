package pe.cayro.sam;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.api.RestClient;
import pe.cayro.sam.model.Institution;
import util.Constants;

public class WelcomeActivity extends AppCompatActivity {

    private static String TAG = WelcomeActivity.class.getSimpleName();

    private static final int SPLASH_DURATION = 1500;
    private boolean mIsBackButtonPressed;
    private ProgressDialog progress;
    @Bind(R.id.logo_sam)
    ImageView logo;
    private Handler handler;


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
                    progress.setMax(14);
                    progress.setMessage("Sincronizando");
                    progress.show();
                    new LoginAsyncTask(getApplicationContext()).execute("");
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

            //Log.i(TAG, telephonyManager.getDeviceId());
            //this.publishProgress("Obteniendo IMEI");

            //// TODO: 17/07/15
            //// Replace for implement in production enable the following line code
            //// String imei = telephonyManager.getDeviceId();
            //String imei = Constants.IMEI_TEST;

            Realm realm = Realm.getInstance(getApplicationContext());

            this.publishProgress("Cargando Instituciones");

            try{

                realm.beginTransaction();
                realm.clear(Institution.class);

                List<Institution> institutions = RestClient.get().getListInstitutions();

                realm.copyToRealmOrUpdate(institutions);

                realm.commitTransaction();

                editor.putString(Constants.CYCLE_LOADED, Constants.YES);
                editor.apply();


            } finally {
            if (realm != null) {
                realm.close();
            }
        }

            //Log.i(TAG, imei);

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
