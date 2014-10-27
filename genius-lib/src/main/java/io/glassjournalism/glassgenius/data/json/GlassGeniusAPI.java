package io.glassjournalism.glassgenius.data.json;

import java.util.List;

import retrofit.Callback;
import retrofit.http.POST;

public interface GlassGeniusAPI {
    @POST("/category")
    void getCategories(Callback<List<Category>> categories);

    @POST("/card")
    void getCards(Callback<List<Card>> card);
}
