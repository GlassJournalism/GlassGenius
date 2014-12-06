package io.glassjournalism.glassgenius.data.json;

import com.google.gson.JsonArray;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

public interface GlassGeniusAPI {

    public static GlassGeniusAPI GlassGeniusAPI = new RestAdapter.Builder()
            .setEndpoint(Constants.API_ROOT)
            .build().create(GlassGeniusAPI.class);

    @GET("/category")
    public abstract void getCategories(Callback<List<Category>> categories);

    @GET("/card")
    public abstract void getCards(Callback<List<GeniusCard>> cb);

    @GET("/card?fields=id")
    public abstract void getAllCardIDs(Callback<List<CardFieldResponse>> cb);

    @GET("/card/find")
    public abstract void findCard(@Header("Session-Id") String sessionId, @Query("text") String text, Callback<List<CardFoundResponse>> cb);

    @GET("/card/triggers")
    public abstract void getTriggers(Callback<JsonArray> cb);

    @GET("/video")
    public abstract void getAllVideos(Callback<List<VideoResponse>> cb);

    @GET("/articles")
    public abstract void getArticles(Callback<List<Article>> cb);

}
