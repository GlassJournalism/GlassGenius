package io.glassjournalism.glassgenius.data.json;

import com.google.gson.JsonArray;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GlassGeniusAPI {
    @GET("/category")
    void getCategories(Callback<List<Category>> categories);

    @GET("/card")
    void getCards(Callback<List<GeniusCard>> cb);

    @GET("/card?fields=id")
    void getAllCardIDs(Callback<List<JsonArray>> cb);

    @GET("/card/find")
    void findCard(@Query("text") String text, Callback<JsonArray> cb);

    @GET("/card/triggers")
    void getTriggers(Callback <JsonArray> cb);

}
