package pe.cayro.sam.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by David on 12/01/16.
 */
public class RecordDetail extends RealmObject {

    @PrimaryKey
    private String uuid;
    private String recordUuid;
    private int productId;
    private int qty;
    private float qtyCalculated;
    private Record record;
    private Product product;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRecordUuid() {
        return recordUuid;
    }

    public void setRecordUuid(String recordUuid) {
        this.recordUuid = recordUuid;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public float getQtyCalculated() {
        return qtyCalculated;
    }

    public void setQtyCalculated(float qtyCalculated) {
        this.qtyCalculated = qtyCalculated;
    }
}
