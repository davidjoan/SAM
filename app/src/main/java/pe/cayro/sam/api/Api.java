package pe.cayro.sam.api;

import java.util.List;

import pe.cayro.sam.model.Agent;
import pe.cayro.sam.model.AttentionType;
import pe.cayro.sam.model.Doctor;
import pe.cayro.sam.model.Institution;
import pe.cayro.sam.model.Product;
import pe.cayro.sam.model.Specialty;
import pe.cayro.sam.model.User;
import retrofit.http.GET;
import retrofit.http.Path;
import util.Constants;

/**
 * Created by David on 8/01/16.
 */
public interface Api {

    @GET(Constants.API_INSTITUTION)
    List<Institution> getListInstitutions();

    @GET(Constants.API_PRODUCT)
    List<Product> getListProducts();

    @GET(Constants.API_SPECIALTY)
    List<Specialty> getListSpecialties();

    @GET(Constants.API_DOCTOR)
    List<Doctor> getListDoctors();

    @GET(Constants.API_ATTENTION_TYPE)
    List<AttentionType> getAttentionTypes();

    @GET("/users/{imei}")
    User getUserByImei(@Path(Constants.IMEI) String imei);

    @GET(Constants.API_AGENT)
    List<Agent> getAgents();

}
