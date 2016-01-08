package pe.cayro.sam.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by David on 7/01/16.
 */
public class Institution extends RealmObject {

    @PrimaryKey
    private int id;
    private String code;
    private String name;
    private String address;
    private float latitude;
    private float longitude;
    private boolean active;
    private RealmList<Doctor> doctors;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public RealmList<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(RealmList<Doctor> doctors) {
        this.doctors = doctors;
    }
}
