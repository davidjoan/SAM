package pe.cayro.sam.api;

import java.util.List;

import pe.cayro.sam.model.Agent;
import pe.cayro.sam.model.AttentionType;
import pe.cayro.sam.model.Doctor;
import pe.cayro.sam.model.Institution;
import pe.cayro.sam.model.Product;
import pe.cayro.sam.model.Specialty;
import pe.cayro.sam.model.Ubigeo;
import pe.cayro.sam.model.User;
import retrofit.http.GET;
import retrofit.http.Query;
import util.Constants;

/**
 * Created by David on 8/01/16.
 */
public interface Api {

    @GET(Constants.API_INSTITUTION)
    List<Institution> getListInstitutions(@Query(Constants.ID_KEY) String imei,
                                          @Query(Constants.ID_USUARIO) int idUsuario );

    @GET(Constants.API_PRODUCT)
    List<Product> getListProducts(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_SPECIALTY)
    List<Specialty> getListSpecialties(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_DOCTOR)
    List<Doctor> getListDoctors(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_ATTENTION_TYPE)
    List<AttentionType> getAttentionTypes(@Query(Constants.ID_KEY) String imei);

    @GET("/usersIMEI")
    List<User> getUserByImei(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_AGENT)
    List<Agent> getAgents(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_UBIGEO)
    List<Ubigeo> getUbigeos(@Query(Constants.ID_KEY) String imei);

}
