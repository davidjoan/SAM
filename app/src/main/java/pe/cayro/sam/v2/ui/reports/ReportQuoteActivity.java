package pe.cayro.sam.v2.ui.reports;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.v2.R;
import pe.cayro.sam.v2.api.RestClient;
import pe.cayro.sam.v2.model.User;
import pe.cayro.sam.v2.model.report.InstitutionReport;
import pe.cayro.sam.v2.util.Constants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReportQuoteActivity extends AppCompatActivity {

    Realm realm;
    User user;
    @Bind(R.id.report_recycler_view)
    protected RecyclerView mRecyclerView;

    private List<InstitutionReport> reportList;
    private ReportQuoteListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_quote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        ButterKnife.bind(this);

        user = realm.where(User.class).findFirst();

        reportList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReportQuoteListAdapter(reportList,R.layout.quote_item);
        mRecyclerView.setAdapter(mAdapter);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        getData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Actualizando Información de Cuotas", Snackbar.LENGTH_LONG)
                        .show();
                getData();

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void getData(){
        RestClient.get().getShareInstitution(user.getId(), new Callback<List<InstitutionReport>>() {
            @Override
            public void success(List<InstitutionReport> institutionReports, Response response) {
                mAdapter.setData(institutionReports);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    public class ReportQuoteListAdapter extends RecyclerView.
            Adapter<ReportQuoteListAdapter.ViewHolder> {

        private List<InstitutionReport> items;
        private int itemLayout;

        public ReportQuoteListAdapter(List<InstitutionReport> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<InstitutionReport> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(itemLayout,
                    parent, false);
            return new ViewHolder(itemLayoutView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            InstitutionReport item = items.get(position);
            viewHolder.name.setText(item.getName());
            viewHolder.address.setText(item.getAddress());
            viewHolder.id = item.getId();
            viewHolder.dayValue = item.getPorcday();
            viewHolder.weekValue = item.getPorcweek();
            viewHolder.monthValue = item.getPorcmonth();

            viewHolder.day.setText("Día: "+ String.format("%.2f", item.getPorcday())+"%");
            viewHolder.week.setText("Semana: "+ String.format("%.2f", item.getPorcweek())+"%");
            viewHolder.month.setText("Mes: "+ String.format("%.2f", item.getPorcmonth())+ "%");

            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {

            public TextView name;
            public TextView address;
            public TextView day;
            public TextView week;
            public TextView month;

            public float dayValue;
            public float weekValue;
            public float monthValue;
            public int id;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.report_quote_name);
                address = (TextView) itemView.findViewById(R.id.report_quote_address);
                day = (TextView) itemView.findViewById(R.id.report_quote_day);
                week = (TextView) itemView.findViewById(R.id.report_quote_week);
                month = (TextView) itemView.findViewById(R.id.report_quote_month);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                        Intent intent = new Intent(getApplication(), ReportDetailQuoteActivity.class);
                        intent.putExtra(Constants.ID, id);
                        intent.putExtra(Constants.INSTITUTION_NAME, name.getText());

                        intent.putExtra(Constants.DAY, dayValue);
                        intent.putExtra(Constants.WEEK, weekValue);
                        intent.putExtra(Constants.MONTH, monthValue);
                        startActivity(intent);
            }
        }
    }



}
