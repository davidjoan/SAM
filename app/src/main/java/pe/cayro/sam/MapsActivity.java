package pe.cayro.sam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import pe.cayro.sam.model.Institution;
import util.Constants;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);
        toolbar.setTitle(R.string.title_activity_map);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        Realm realm = Realm.getDefaultInstance();

        RealmResults<Institution> institutionList = realm.where(Institution.class).findAll();
        institutionList.sort(Constants.LATITUDE, Sort.DESCENDING);

        for (Institution institution: institutionList) {
            LatLng sydney = new LatLng(institution.getLatitude(), institution.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title(institution.getName()));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(sydney).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
        }
    }
}
