package vn.edu.usth.recipefoodapp.fragment_ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.recipefoodapp.ApiClient;
import vn.edu.usth.recipefoodapp.R;
import vn.edu.usth.recipefoodapp.activity_ui.MainActivity;
import vn.edu.usth.recipefoodapp.adapter_ui.CategoryAdapter;
import vn.edu.usth.recipefoodapp.adapter_ui.MealAdapter;
import vn.edu.usth.recipefoodapp.object_ui.Category;
import vn.edu.usth.recipefoodapp.object_ui.MealSimple;
import vn.edu.usth.recipefoodapp.response.CategoryResponse;
import vn.edu.usth.recipefoodapp.response.MealListResponse;

public class CategoryFragment extends Fragment {
    private RecyclerView rvCategories, rvMeals;
    private ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        rvCategories = view.findViewById(R.id.rvCategories);
        rvMeals = view.findViewById(R.id.rvMeals);
        progress = view.findViewById(R.id.progressMain);

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMeals.setLayoutManager(new GridLayoutManager(getContext(), 2));

        loadCategories();

        return view;
    }

    private void loadCategories() {
        progress.setVisibility(View.VISIBLE);
        ApiClient.getMealApi().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().categories;
                    if (categories != null && !categories.isEmpty()) {

                        // Read the Dark Mode setting from SharedPreferences
                        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
                        boolean isDarkMode = prefs.getBoolean(MainActivity.KEY_DARK_MODE, false);

                        // Pass the isDarkMode flag to the adapter's constructor
                        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
                            loadMealsByCategory(category.strCategory);
                        }, isDarkMode);

                        rvCategories.setAdapter(adapter);

                        // Load meals of the first category by default
                        loadMealsByCategory(categories.get(0).strCategory);
                    }
                } else {
                    Toast.makeText(getContext(), "Unable to get categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMealsByCategory(String category) {
        progress.setVisibility(View.VISIBLE);
        ApiClient.getMealApi().getMealsByCategory(category).enqueue(new Callback<MealListResponse>() {
            @Override
            public void onResponse(Call<MealListResponse> call, Response<MealListResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<MealSimple> meals = response.body().meals;
                    rvMeals.setAdapter(new MealAdapter(getContext(), meals));
                } else {
                    Toast.makeText(getContext(), "Unable to get meals", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MealListResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}