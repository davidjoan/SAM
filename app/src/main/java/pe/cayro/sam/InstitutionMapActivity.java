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
import pe.cayro.sam.model.Tracking;

public class InstitutionMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    String trackingUuid;

    Realm realm;

    Tracking tracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        realm = Realm.getInstance(getApplicationContext());

        trackingUuid = getIntent().getStringExtra("tracking_uuid");

        tracking = realm.where(Tracking.class).equalTo("uuid", trackingUuid).findFirst();

        toolbar.setTitle(tracking.getInstitution().getName());
        toolbar.setSubtitle(tracking.getInstitution().getAddress());

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

        LatLng sydney = new LatLng(tracking.getLatitude(), tracking.getLongitude());

        mMap.addMarker(new MarkerOptions().position(sydney).title(tracking.getInstitution().getName()));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sydney).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

    }
}
