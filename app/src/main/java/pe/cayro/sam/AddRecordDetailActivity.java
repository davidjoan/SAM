package pe.cayro.sam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.sam.adapter.ProductAutocompleterAdapter;
import pe.cayro.sam.model.Product;
import pe.cayro.sam.model.Record;
import pe.cayro.sam.model.RecordDetail;
import util.Constants;

public class AddRecordDetailActivity extends AppCompatActivity {

    private static String TAG = AddRecordDetailActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.record_detail_add_record_detail)
    protected Button buttonSave;
    @Bind(R.id.record_detail_cancel_record_detail)
    protected Button buttonCancel;
    @Bind(R.id.record_detail_qty)
    protected EditText recordDetailQty;
    @Bind(R.id.record_detail_list)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.record_detail_qty_calculated)
    protected EditText recordDetailQtyCalculated;
    @Bind(R.id.record_detail_product_autocompleter)
    protected AppCompatAutoCompleteTextView recordDetailProduct;

    private static final DecimalFormat oneDecimal = new DecimalFormat("#");

    private Realm realm;
    private Record record;
    private String recordUuid;
    private int recordCode;
    private Product product;
    private List<RecordDetail> recordDetails;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecordDetailListAdapter mAdapter;
    private ProductAutocompleterAdapter adapterProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_detail);

        recordUuid = getIntent().getStringExtra(Constants.UUID);
        recordCode = getIntent().getIntExtra(Constants.CODE, 0);

        ButterKnife.bind(this);

        oneDecimal.setRoundingMode(RoundingMode.DOWN);

        toolbar.setTitle(R.string.medical_sample);
        toolbar.setSubtitle(Constants.CODE_FIELD + String.valueOf(recordCode));

        setSupportActionBar(toolbar);

        adapterProduct = new ProductAutocompleterAdapter(this, R.layout.product_autocomplete_item);
        recordDetailProduct.setAdapter(adapterProduct);
        recordDetailProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer temp = adapterProduct.getItem(position);

                product = realm.where(Product.class).equalTo(Constants.ID,
                        temp.intValue()).findFirst();
                recordDetailProduct.setText(product.getName());
            }
        });

        realm = Realm.getDefaultInstance();

        record = realm.where(Record.class).equalTo(Constants.UUID,recordUuid).findFirst();

        recordDetails = realm.where(RecordDetail.class).equalTo("recordUuid",recordUuid).findAll();

        if(record.getAttentionTypeId() == 2){

            recordDetailQtyCalculated.setVisibility(View.VISIBLE);

        }else{
            recordDetailQtyCalculated.setVisibility(View.GONE);
        }

        recordDetailQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(recordDetailQty.getText().length() > 0){
                    Float value =  Float.valueOf(recordDetailQty.getText().toString());

                    recordDetailQtyCalculated.setText(String.valueOf(oneDecimal.format(value.floatValue()/2)));
                }else{
                    recordDetailQtyCalculated.setText("");
                }
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecordDetailListAdapter(recordDetails, R.layout.record_detail_item);
        mRecyclerView.setAdapter(mAdapter);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    getParent().setResult(Activity.RESULT_OK, intent);
                }

                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int errors = 0;
                if (recordDetailQty.getText().length() == 0) {
                    errors++;
                    recordDetailQty.setError("La cantidad no puede estar en blanco");
                }
                if (recordDetailProduct.getText().length() == 0) {
                    errors++;
                    recordDetailProduct.setError("La Muestra Médica no puede estar en blanco");
                }
                if(product == null){
                    errors++;
                    recordDetailProduct.setError("La Muestra Médica no es correcta");
                }

                if (errors == 0) {

                    realm.beginTransaction();

                    RecordDetail recordDetail = realm.createObject(RecordDetail.class);
                    recordDetail.setRecord(record);
                    recordDetail.setProduct(product);
                    recordDetail.setProductId(product.getId());
                    recordDetail.setRecordUuid(record.getUuid());
                    recordDetail.setUuid(UUID.randomUUID().toString());
                    recordDetail.setQty(Integer.valueOf(recordDetailQty.getText().toString()).intValue());
                    recordDetail.setQtyCalculated(Float.valueOf(recordDetailQtyCalculated.getText().toString()).floatValue());

                    realm.copyToRealm(recordDetail);
                    record.getRecordDetails().add(recordDetail);
                    realm.copyToRealm(record);
                    realm.commitTransaction();

                    recordDetailProduct.setText("");
                    recordDetailQty.setText("");
                    recordDetailQtyCalculated.setText("");

                    recordDetails = realm.where(RecordDetail.class)
                            .equalTo("recordUuid", recordUuid).findAll();
                    mAdapter.setData(recordDetails);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public class RecordDetailListAdapter extends RecyclerView.
            Adapter<RecordDetailListAdapter.ViewHolder> {

        private List<RecordDetail> items;
        private int itemLayout;

        public RecordDetailListAdapter(List<RecordDetail> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<RecordDetail> items) {
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
            RecordDetail item = items.get(position);
            viewHolder.name.setText(item.getProduct().getName());
            if(item.getRecord().getAttentionTypeId() == 2) {
                viewHolder.qty.setText(Constants.QTY_FIELD + String.valueOf(item.getQty())+
                        ", C Calculada: "+String.valueOf(item.getQtyCalculated()));
            }else{
                viewHolder.qty.setText(Constants.QTY_FIELD + String.valueOf(item.getQty()));
            }
            viewHolder.uuid = item.getUuid();

            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {

            public TextView name;
            public TextView qty;
            public String uuid;

            public ViewHolder(View itemView) {
                super(itemView);
                name    = (TextView) itemView.findViewById(R.id.record_detail_name);
                qty = (TextView) itemView.findViewById(R.id.record_detail_qty);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                /* TODO: Implement Intent to edit the patient information */
            }
        }
    }

    /**
     * Exit the app if user select yes.
     */
    private void doExit() {
      /*  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton(Constants.SI, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        alertDialog.setNegativeButton(Constants.NO, null);
        alertDialog.setMessage(Constants.LOGOUT_3);
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.show();*/
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