package vn.edu.usth.recipefoodapp.fragment_ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.recipefoodapp.ApiClient;
import vn.edu.usth.recipefoodapp.R;
import vn.edu.usth.recipefoodapp.adapter_ui.MealAdapter;
import vn.edu.usth.recipefoodapp.object_ui.MealSimple;
import vn.edu.usth.recipefoodapp.response.MealListResponse;

public class HomeFragment extends Fragment {

    private RecyclerView rvMeals;
    private ProgressBar progress;
    private EditText edtSearch;
    private ImageButton btnSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvMeals = view.findViewById(R.id.rvMeals);
        progress = view.findViewById(R.id.progressMain);
        edtSearch = view.findViewById(R.id.edtSearch);
        btnSearch = view.findViewById(R.id.btnSearch);

        //divide to items in home activity to 2 columns
        rvMeals.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Load random meals initially
        loadRandomMeals();

        // Search button
        btnSearch.setOnClickListener(v -> doSearch());

        // Press Enter on keyboard
        edtSearch.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN)) {
                doSearch();
                return true;
            }
            return false;
        });

        return view;
    }

    private void doSearch() {
        String keyword = edtSearch.getText().toString().trim();
        if (!keyword.isEmpty()) {
            searchMeals(keyword);
        } else {
            Toast.makeText(getContext(), "Please enter a meal name", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRandomMeals() {
        progress.setVisibility(View.VISIBLE);
        List<MealSimple> meals = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            ApiClient.getMealApi().getRandomMeal().enqueue(new Callback<MealListResponse>() {
                @Override
                public void onResponse(Call<MealListResponse> call, Response<MealListResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().meals != null) {
                        meals.addAll(response.body().meals);
                        if (meals.size() == 40) {
                            progress.setVisibility(View.GONE);
                            rvMeals.setAdapter(new MealAdapter(getContext(), meals));
                        }
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

    private void searchMeals(String keyword) {
        progress.setVisibility(View.VISIBLE);
        ApiClient.getMealApi().searchMeals(keyword).enqueue(new Callback<MealListResponse>() {
            @Override
            public void onResponse(Call<MealListResponse> call, Response<MealListResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().meals != null) {
                    List<MealSimple> meals = response.body().meals;
                    rvMeals.setAdapter(new MealAdapter(getContext(), meals));
                } else {
                    Toast.makeText(getContext(), "No meal found", Toast.LENGTH_SHORT).show();
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
