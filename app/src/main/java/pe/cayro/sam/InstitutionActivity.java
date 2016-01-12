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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.model.Institution;
import pe.cayro.sam.model.Tracking;
import pe.cayro.sam.ui.FragmentInstitution;
import pe.cayro.sam.ui.FragmentTracking;

public class InstitutionActivity extends AppCompatActivity {

    private static String TAG = InstitutionActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    Realm realm;
    String trackingCode;

    Tracking tracking;
    Institution institution;
    String institutionName;


    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawer;

    @Bind(R.id.nvView)
    protected NavigationView nvDrawer;

    private ActionBarDrawerToggle drawerToggle;
    FragmentManager fragmentManager;

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

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,
                FragmentInstitution.newInstance()).commit();

        // Highlight the selected item, update the title, and close the drawer
        setupDrawerContent(nvDrawer);
        nvDrawer.getMenu().getItem(0).setChecked(true);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close){
            @Override
            public void onDrawerClosed(View view) {
                // your refresh code can be called from here
            }
        };
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {

                        // Highlight the selected item, update the title, and close the drawer
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
                    fragment = FragmentInstitution.newInstance();
                    break;
                case R.id.nav_second_fragment:
                    fragment = FragmentInstitution.newInstance();
                    break;
                case R.id.nav_third_fragment:
                    fragment = FragmentInstitution.newInstance();
                    break;
                case R.id.nav_fourth_fragment:
                    fragment = FragmentTracking.newInstance();
                    break;
                case R.id.nav_five_fragment:
                    fragment = FragmentInstitution.newInstance();
                    break;

                default:
                    fragment = FragmentInstitution.newInstance();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


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