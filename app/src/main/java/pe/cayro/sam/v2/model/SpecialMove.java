package pe.cayro.sam.v2.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by David on 4/23/16.
 *
 *
 */
public class SpecialMove extends RealmObject {

    @PrimaryKey
    private String uuid;
    private int typeMovementId;
    private String recordDate;
    private String reasonId;
    private String comment;
    private String userId;
    private Date createdAt;
    private boolean active;
    private boolean sent;

    private TypeMovement typeMovement;

    private RealmList<SpecialMoveDetail> specialMoveDetails;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getTypeMovementId() {
        return typeMovementId;
    }

    public void setTypeMovementId(int typeMovementId) {
        this.typeMovementId = typeMovementId;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getReasonId() {
        return reasonId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public RealmList<SpecialMoveDetail> getSpecialMoveDetails() {
        return specialMoveDetails;
    }

    public void setSpecialMoveDetails(RealmList<SpecialMoveDetail> specialMoveDetails) {
        this.specialMoveDetails = specialMoveDetails;
    }

    public TypeMovement getTypeMovement() {
        return typeMovement;
    }

    public void setTypeMovement(TypeMovement typeMovement) {
        this.typeMovement = typeMovement;
    }
}
