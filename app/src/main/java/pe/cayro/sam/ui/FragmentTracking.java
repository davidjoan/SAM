package pe.cayro.sam.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.InstitutionMapActivity;
import pe.cayro.sam.R;
import pe.cayro.sam.model.Tracking;
import util.Constants;

/**
 * Created by David on 8/01/16.
 */
public class FragmentTracking extends Fragment {
    private static String TAG = FragmentTracking.class.getSimpleName();

    @Bind(R.id.institution_recycler_view)
    protected RecyclerView mRecyclerView;

    private Realm realm;
    private List<Tracking> trackingList;
    private TrackingListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.assistance);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();
        trackingList = realm.where(Tracking.class).findAll();

        Log.d(TAG, Constants.QTY_FIELD+String.valueOf(trackingList.size()));

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
            String typeString = Constants.EMPTY;

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATETIME_SLASH);
            String dateFormat = formatter.format(item.getCreatedAt());

            if(item.getType().equals(Constants.LOGIN)){
                typeString = Constants.LOGIN_AT;
            }else if(item.getType().equals(Constants.LOGOUT)){
                typeString = Constants.LOGOUT_AT;
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
                Context context = itemView.getContext();
                Intent intent = new Intent(getActivity(), InstitutionMapActivity.class);
                intent.putExtra(Constants.TRACKING_UUID, uuid);
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
