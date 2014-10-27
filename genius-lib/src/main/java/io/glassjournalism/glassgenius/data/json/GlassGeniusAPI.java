package io.glassjournalism.glassgenius.data.json;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface GlassGeniusAPI {
    @GET("/category")
    void getCategories(Callback<List<Category>> categories);

    @GET("/card")
    void getCards(Callback<List<GeniusCard>> card);
}
