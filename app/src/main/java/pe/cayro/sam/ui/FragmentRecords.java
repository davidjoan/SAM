package pe.cayro.sam.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.NewRecordActivity;
import pe.cayro.sam.R;
import pe.cayro.sam.model.Record;
import pe.cayro.sam.model.Tracking;
import util.Constants;

/**
 * Created by David on 8/01/16.
 */
public class FragmentRecords extends Fragment {
    private static String TAG = FragmentRecords.class.getSimpleName();

    static final int ADD_RECORD_REQUEST = 1;

    @Bind(R.id.record_recycler_view)
    protected RecyclerView mRecyclerView;

    protected String trackingUuid;
    protected Realm realm;
    protected List<Record> recordList;
    private RecordListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected Tracking tracking;

    public static FragmentRecords newInstance(String uuid) {
        Bundle args = new Bundle();
        FragmentRecords fragment = new FragmentRecords();
        args.putString(Constants.UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackingUuid = getArguments().getString(Constants.UUID);
        Log.i(TAG, trackingUuid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_record,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.record_title);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.medical_sample);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();

        tracking = realm.where(Tracking.class).equalTo(Constants.UUID, trackingUuid).findFirst();

        recordList = realm.where(Record.class).findAll();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecordListAdapter(recordList, R.layout.record_item);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_record, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new_record:
                Intent intent = new Intent(getActivity(), NewRecordActivity.class);
                intent.putExtra(Constants.TRACKING_UUID,tracking.getUuid());
                startActivityForResult(intent, ADD_RECORD_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class RecordListAdapter extends RecyclerView.
            Adapter<RecordListAdapter.ViewHolder> {

        private List<Record> items;
        private int itemLayout;

        public RecordListAdapter(List<Record> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Record> items) {
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
            Record item = items.get(position);

            viewHolder.doctor.setText(Constants.DOCTOR_ABR+item.getDoctor().getName());
            viewHolder.patient.setText(item.getPatient().getName());
            viewHolder.attentionType.setText(item.getAttentionType().getName());
            String dateFormat = "";
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATETIME_SLASH);
            dateFormat = formatter.format(item.getRecordDate());

            viewHolder.date.setText(dateFormat);
            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {

            public TextView doctor;
            public TextView patient;
            public TextView attentionType;
            public TextView date;

            public ViewHolder(View itemView) {
                super(itemView);
                doctor = (TextView) itemView.findViewById(R.id.record_doctor);
                patient = (TextView) itemView.findViewById(R.id.record_patient);
                attentionType = (TextView) itemView.findViewById(R.id.record_attention_type);
                date = (TextView) itemView.findViewById(R.id.record_date);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                /* TODO: Implement Intent to edit the record information */
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}