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
import pe.cayro.sam.ui.FragmentInstitution;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawer;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.nvView)
    protected NavigationView nvDrawer;

    private ActionBarDrawerToggle drawerToggle;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.app_name));

        toolbar.setLogoDescription(getResources().getString(R.string.app_name));

        setSupportActionBar(toolbar);

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
                    fragment = FragmentInstitution.newInstance();
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
