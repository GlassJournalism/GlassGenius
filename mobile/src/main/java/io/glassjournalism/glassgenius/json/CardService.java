package io.glassjournalism.glassgenius.json;

import java.util.List;

import retrofit.Callback;
import retrofit.http.POST;

/**
 * Created by ian on 10/22/14.
 */
public interface CardService {
    @POST("/card")
    void getCards(Callback<List<Card>> card);
}
