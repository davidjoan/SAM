package pe.cayro.sam;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.model.User;
import pe.cayro.sam.ui.FragmentInstitution;
import pe.cayro.sam.ui.FragmentTracking;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.drawer_layout_main)
    protected DrawerLayout mDrawer;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.nvViewMain)
    protected NavigationView nvDrawer;

    TextView userName;

    TextView userCode;

    private ActionBarDrawerToggle drawerToggle;

    FragmentManager fragmentManager;

    User user;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.app_name));

        toolbar.setLogoDescription(getResources().getString(R.string.app_name));

        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();




        final ActionBar ab = getSupportActionBar();

        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);

        ab.setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();

        mDrawer.setDrawerListener(drawerToggle);

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.flContent,
                FragmentInstitution.newInstance()).commit();

        setupDrawerContent(nvDrawer);

        nvDrawer.getMenu().getItem(0).setChecked(true);


        //View header = findViewById(R.id.nvViewMain);


        View header = nvDrawer.inflateHeaderView(R.layout.nav_header);

        userName = (TextView) header.findViewById(R.id.NavUserName);
        userName.setText(user.getName());
        userCode = (TextView) header.findViewById(R.id.NavUserCode);
        userCode.setText(user.getCode());

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
                    fragment = FragmentTracking.newInstance();
                    break;
                case R.id.nav_third_fragment:
                    fragment = FragmentInstitution.newInstance();
                    break;
                case R.id.nav_fourth_fragment:
                    fragment = FragmentInstitution.newInstance();
                    break;
                case R.id.nav_five_fragment:
                    fragment = FragmentInstitution.newInstance();
                    doExit();
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

        if (id == R.id.action_map) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Exit the app if user select yes.
     */
    private void doExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("¿Desea Salir?");
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.show();
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
