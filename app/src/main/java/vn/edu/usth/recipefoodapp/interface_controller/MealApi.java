package vn.edu.usth.recipefoodapp.interface_controller;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.edu.usth.recipefoodapp.response.CategoryResponse;
import vn.edu.usth.recipefoodapp.response.MealDetailResponse;
import vn.edu.usth.recipefoodapp.response.MealListResponse;

public interface MealApi {
    @GET("categories.php")
    Call<CategoryResponse> getCategories();

    @GET("filter.php")
    Call<MealListResponse> getMealsByCategory(@Query("c") String category);

    @GET("lookup.php")
    Call<MealDetailResponse> getMealDetails(@Query("i") String id);

    @GET("random.php")
    Call<MealListResponse> getRandomMeal();

    @GET("search.php")
    Call<MealListResponse> searchMeals(@Query("s") String mealName);
}
