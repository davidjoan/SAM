package pe.cayro.sam;

import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.model.Institution;
import pe.cayro.sam.model.Tracking;
import pe.cayro.sam.ui.FragmentDoctor;
import pe.cayro.sam.ui.FragmentInstitution;
import pe.cayro.sam.ui.FragmentPatient;
import pe.cayro.sam.ui.FragmentRecords;
import pe.cayro.sam.ui.FragmentTracking;
import util.Constants;

public class InstitutionActivity extends AppCompatActivity {
    private static String TAG = InstitutionActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawer;
    @Bind(R.id.nvView)
    protected NavigationView nvDrawer;

    private Realm realm;
    private Tracking tracking;
    private String trackingCode;
    private String institutionName;
    private Institution institution;
    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution);

        trackingCode = getIntent().getStringExtra(Constants.TRACKING_CODE);
        institutionName = getIntent().getStringExtra(Constants.INSTITUTION_NAME);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        tracking = realm.where(Tracking.class).equalTo(Constants.CODE, trackingCode).findFirst();

        institution = realm.where(Institution.class)
                .equalTo(Constants.ID, tracking.getInstitutionId()).findFirst();

        toolbar.setTitle(institutionName);
        toolbar.setSubtitle(trackingCode);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,
                FragmentRecords.newInstance(tracking.getUuid())).commit();

        setupDrawerContent(nvDrawer);
        nvDrawer.getMenu().getItem(0).setChecked(true);

        View header = nvDrawer.inflateHeaderView(R.layout.institution_header);

        TextView institutionNameTextView = (TextView) header.findViewById(R.id.institution_name);
        institutionNameTextView.setText(institution.getName());
        TextView institutionCodeTextView = (TextView) header.findViewById(R.id.tracking_code);
        institutionCodeTextView.setText(Constants.CODE_FIELD+tracking.getCode());
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close){
            @Override
            public void onDrawerClosed(View view) {
            }
        };
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        mDrawer.closeDrawers();
                        menuItem.setChecked(true);
                        setTitle(menuItem.getTitle());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                selectDrawerItem(menuItem);
                            }
                        }, 260);

                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        try {
            switch(menuItem.getItemId()) {
                case R.id.nav_first_fragment:
                    fragment = FragmentRecords.newInstance(tracking.getUuid());
                    break;
                case R.id.nav_second_fragment:
                    fragment = FragmentDoctor.newInstance();
                    break;
                case R.id.nav_third_fragment:
                    fragment = FragmentPatient.newInstance();
                    break;
                case R.id.nav_fourth_fragment:
                    fragment = FragmentTracking.newInstance();
                    break;
                case R.id.nav_five_fragment:
                    fragment = FragmentInstitution.newInstance();
                    break;

                default:
                    fragment = FragmentRecords.newInstance(tracking.getUuid());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_institution, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
