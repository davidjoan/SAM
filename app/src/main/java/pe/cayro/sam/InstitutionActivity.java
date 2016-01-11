package pe.cayro.sam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.model.Institution;
import pe.cayro.sam.model.Tracking;

public class InstitutionActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    Realm realm;
    String trackingCode;

    Tracking tracking;
    Institution institution;
    String institutionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution);

        trackingCode = getIntent().getStringExtra("tracking_code");
        institutionName = getIntent().getStringExtra("institution_name");

        ButterKnife.bind(this);

        realm = Realm.getInstance(getApplicationContext());

        tracking = realm.where(Tracking.class).equalTo("code", trackingCode).findFirst();

        //institution = realm.where(Institution.class).equalTo("id",tracking.getInstitutionId()).findFirst();

        toolbar.setTitle(institutionName);
        toolbar.setSubtitle(trackingCode);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_institution, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
