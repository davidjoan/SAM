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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.R;
import pe.cayro.sam.model.Doctor;
import pe.cayro.sam.model.Specialty;
import util.Constants;

/**
 * Created by David on 8/01/16.
 */
public class FragmentDoctor extends Fragment {
    private static String TAG = FragmentDoctor.class.getSimpleName();

    @Bind(R.id.doctor_recycler_view)
    protected RecyclerView mRecyclerView;

    Realm realm;
    List<Doctor> doctorList;
    private DoctorListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public static FragmentDoctor newInstance() {
        Bundle args = new Bundle();

        FragmentDoctor fragment = new FragmentDoctor();
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
        View view =inflater.inflate(R.layout.fragment_doctor,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Constants.DOCTORS);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();

        doctorList = realm.where(Doctor.class).findAll();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(Constants.QTY_FIELD+
                String.valueOf(doctorList.size()));

        Log.d(TAG, Constants.QTY_FIELD+String.valueOf(doctorList.size()));

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DoctorListAdapter(doctorList, R.layout.doctor_item);
        mRecyclerView.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_doctor, menu);
    }

    public class DoctorListAdapter extends RecyclerView.
            Adapter<DoctorListAdapter.ViewHolder> {

        private List<Doctor> items;
        private int itemLayout;

        public DoctorListAdapter(List<Doctor> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Doctor> items) {
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
            Doctor item = items.get(position);

            viewHolder.name.setText(item.getName());
            viewHolder.code.setText(Constants.CMP_FIELD+item.getCode());
            viewHolder.specialty.setText(Constants.SPECIALTY_FIELD+item.getSpecialty().getName());
            viewHolder.uuid = item.getUuid();

            Picasso.with(getContext()).
                    load(new StringBuilder().append(Constants.CMP_PHOTO_SERVER)
                            .append(String.format("%05d", Integer.parseInt(item.getCode())))
                            .append(Constants.DOT_JPG).toString()).
                            error(R.drawable.avatar).
                            into(viewHolder.image);

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
            public TextView specialty;
            public TextView code;
            public String uuid;

            public ViewHolder(View itemView) {
                super(itemView);
                name    = (TextView) itemView.findViewById(R.id.doctor_name);
                specialty = (TextView) itemView.findViewById(R.id.doctor_specialty);
                code = (TextView) itemView.findViewById(R.id.doctor_code);
                image = (ImageView) itemView.findViewById(R.id.doctor_image);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                /* TODO: Implement Intent to edit the doctor information */
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}