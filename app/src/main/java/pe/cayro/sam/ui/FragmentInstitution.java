package pe.cayro.sam.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.R;
import pe.cayro.sam.model.Institution;

/**
 * Created by David on 8/01/16.
 */
public class FragmentInstitution extends Fragment {
    private static String TAG = FragmentInstitution.class.getSimpleName();

    @Bind(R.id.institution_recycler_view)
    protected RecyclerView mRecyclerView;

    Realm realm;
    List<Institution> institutionList;
    private InstitutionListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;


    public static FragmentInstitution newInstance() {
        Bundle args = new Bundle();

        FragmentInstitution fragment = new FragmentInstitution();
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

        ButterKnife.bind(this, view);

        realm = Realm.getInstance(getActivity().getApplicationContext());

        institutionList = realm.where(Institution.class).findAll();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new InstitutionListAdapter(institutionList, R.layout.institution_item);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public class InstitutionListAdapter extends RecyclerView.
            Adapter<InstitutionListAdapter.ViewHolder> {

        private List<Institution> items;
        private int itemLayout;

        public InstitutionListAdapter(List<Institution> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Institution> items) {
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
            Institution item = items.get(position);

            viewHolder.name.setText(item.getName());
            viewHolder.address.setText(item.getAddress());
            //viewHolder.image.setImageResource(item.getIcon());
            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {

            public ImageView image;
            public TextView name;
            public TextView address;

            public ViewHolder(View itemView) {
                super(itemView);
              //  image = (ImageView) itemView.findViewById(R.id.institution_name);
                name = (TextView) itemView.findViewById(R.id.institution_name);
                address = (TextView) itemView.findViewById(R.id.institution_address);
            }

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick DEMO");

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @SuppressLint("ValidFragment")
    public class LoginDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            //builder.setMessage("")
            builder.setTitle("Iniciar Sesión");
            builder.setView(inflater.inflate(R.layout.dialog_signin, null))

                    .setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }



}
