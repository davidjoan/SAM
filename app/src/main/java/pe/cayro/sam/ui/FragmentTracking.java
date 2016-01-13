package pe.cayro.sam.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import pe.cayro.sam.InstitutionMapActivity;
import pe.cayro.sam.LoginActivity;
import pe.cayro.sam.R;
import pe.cayro.sam.model.Tracking;
import pe.cayro.sam.model.Tracking;

/**
 * Created by David on 8/01/16.
 */
public class FragmentTracking extends Fragment {
    private static String TAG = FragmentTracking.class.getSimpleName();

    @Bind(R.id.institution_recycler_view)
    protected RecyclerView mRecyclerView;

    Realm realm;
    List<Tracking> trackingList;
    private TrackingListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public static FragmentTracking newInstance() {
        Bundle args = new Bundle();

        FragmentTracking fragment = new FragmentTracking();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_institution,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Asistencia");

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();

        trackingList = realm.where(Tracking.class).findAll();

        Log.d(TAG, "Cantidad de Tracking: "+String.valueOf(trackingList.size()));

        //trackingList.sort("createdAt", Sort.ASCENDING);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TrackingListAdapter(trackingList, R.layout.tracking_item);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public class TrackingListAdapter extends RecyclerView.
            Adapter<TrackingListAdapter.ViewHolder> {

        private List<Tracking> items;
        private int itemLayout;

        public TrackingListAdapter(List<Tracking> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Tracking> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(itemLayout,
                    parent, false);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Tracking item = items.get(position);


            viewHolder.name.setText(item.getInstitution().getName());
            viewHolder.uuid = item.getUuid();


            String typeString = "";
            String dateFormat = "";

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            dateFormat = formatter.format(item.getCreatedAt());

            if(item.getType().equals("login")){
                typeString = "Inicio Sesión el ";
            }
            viewHolder.address.setText(typeString+dateFormat);

            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {

            public ImageView image;
            public TextView name;
            public TextView address;
            public String uuid;

            public ViewHolder(View itemView) {
                super(itemView);
                name    = (TextView) itemView.findViewById(R.id.institution_name);
                address = (TextView) itemView.findViewById(R.id.tracking_name);

                itemView.setOnClickListener(this);
            }


            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick DEMO");
                Context context = itemView.getContext();
                Intent intent = new Intent(getActivity(), InstitutionMapActivity.class);
                intent.putExtra("tracking_uuid", uuid);

                Toast.makeText(getActivity(), uuid, Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
