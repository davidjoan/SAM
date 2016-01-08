package pe.cayro.sam.api;

import java.util.List;

import pe.cayro.sam.model.Institution;
import retrofit.http.GET;
import util.Constants;

/**
 * Created by David on 8/01/16.
 */
public interface Api {

    @GET(Constants.API_INSTITUTION)
    List<Institution> getListInstitutions();
}
