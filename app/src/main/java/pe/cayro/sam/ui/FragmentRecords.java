package pe.cayro.sam.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import pe.cayro.sam.NewRecordActivity;
import pe.cayro.sam.R;
import pe.cayro.sam.model.Record;
import pe.cayro.sam.model.RecordDetail;
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

    private Realm realm;
    private Tracking tracking;
    private String trackingUuid;
    RealmResults<Record> result;
    private RecordListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.medical_sample);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();

        tracking = realm.where(Tracking.class).equalTo(Constants.UUID, trackingUuid).findFirst();

        result = realm.where(Record.class).
                equalTo("institutionId", tracking.getInstitutionId()).findAll();

        result.sort("recordDate", Sort.DESCENDING);

        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setSubtitle(Constants.QTY_FIELD + String.valueOf(result.size()));

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecordListAdapter(result, R.layout.record_item);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        inflater.inflate(R.menu.menu_record, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().
                getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().
                    getComponentName()));
            searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextChange(String data) {

                    if (TextUtils.isEmpty(data)) {
                        result = realm.where(Record.class).
                                equalTo("institutionId",tracking.getInstitutionId()).findAll();
                        result.sort("recordDate", Sort.DESCENDING);

                        mAdapter.setData(result);
                        mAdapter.notifyDataSetChanged();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String data) {
                    if (!TextUtils.isEmpty(data)) {
                        result = realm.where(Record.class).
                                equalTo("institutionId",tracking.getInstitutionId()).beginGroup()
                                .contains("doctor.name", data.toUpperCase())
                                .or()
                                .contains("patient.name", data.toUpperCase())
                                .or()
                                .contains("doctor.code", data.toUpperCase())
                                .or()
                                .contains("patient.code", data.toUpperCase())
                                .endGroup().findAll();

                        result.sort("recordDate", Sort.DESCENDING);

                        mAdapter.setData(result);
                        mAdapter.notifyDataSetChanged();
                    }
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryListener);
        }
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

            if(item.getAttentionTypeId() == 3){
                viewHolder.patient.setText(R.string.record_whitout_patient);

            }else{
                viewHolder.patient.setText(Constants.PATIENT_ABR+item.getPatient().getName());
            }

            viewHolder.attentionType.setText(item.getAttentionType().getName());
            viewHolder.code.setText("#: "+String.valueOf(item.getCode()));

            float sumMM = 0;

            for (RecordDetail temp : item.getRecordDetails()) {
                if(item.getAttentionTypeId() == 2 ) {
                    sumMM = sumMM + temp.getQtyCalculated();
                }else{
                    sumMM = sumMM + temp.getQty();
                }
            }

            viewHolder.mm.setText("mm: "+String.valueOf(sumMM));

            Picasso.with(getContext()).
                    load(new StringBuilder().append(Constants.CMP_PHOTO_SERVER)
                            .append(String.format("%05d",
                                    Integer.parseInt(item.getDoctor().getCode())))
                            .append(Constants.DOT_JPG).toString()).
                    error(R.drawable.avatar).
                    into(viewHolder.image);

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATETIME_SLASH);
            String dateFormat = formatter.format(item.getRecordDate());
            viewHolder.date.setText(Constants.DATE_FIELD+dateFormat);
            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {

            public ImageView image;
            public TextView doctor;
            public TextView code;
            public TextView mm;
            public TextView patient;
            public TextView attentionType;
            public TextView date;

            public ViewHolder(View itemView) {
                super(itemView);
                doctor = (TextView) itemView.findViewById(R.id.record_doctor);
                code = (TextView) itemView.findViewById(R.id.record_code_final);
                mm = (TextView) itemView.findViewById(R.id.record_qty_mm);
                patient = (TextView) itemView.findViewById(R.id.record_patient);
                attentionType = (TextView) itemView.findViewById(R.id.record_attention_type);
                date = (TextView) itemView.findViewById(R.id.record_date);
                image = (ImageView) itemView.findViewById(R.id.record_image);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_RECORD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                result = realm.where(Record.class).
                        equalTo("institutionId", tracking.getInstitutionId()).findAll();
                result.sort("recordDate", Sort.DESCENDING);
                mAdapter.setData(result);
                mAdapter.notifyDataSetChanged();

                ((AppCompatActivity) getActivity()).getSupportActionBar().
                        setSubtitle(Constants.QTY_FIELD+String.valueOf(result.size()));
            }
        }
    }
}