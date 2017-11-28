package com.example.ricardo.bakingapp.utils;

import com.example.ricardo.bakingapp.models.Recipe;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by ricardo on 11/27/17.
 */

public interface ApiService {

    // Set the only endpoint in this case
    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getAllRecipesData();
}
