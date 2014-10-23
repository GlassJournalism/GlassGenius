package io.glassjournalism.glassgenius.json;

import java.util.List;

import retrofit.Callback;
import retrofit.http.POST;

/**
 * Created by ian on 10/22/14.
 */
public interface CategoryService {
    @POST("/category")
    void getCategories(Callback<List<Category>> categories);
}
