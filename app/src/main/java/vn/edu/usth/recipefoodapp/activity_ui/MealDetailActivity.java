package vn.edu.usth.recipefoodapp.activity_ui;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.recipefoodapp.ApiClient;
import vn.edu.usth.recipefoodapp.R;
import vn.edu.usth.recipefoodapp.data.DatabaseClient;
import vn.edu.usth.recipefoodapp.object_ui.MealDetail;
import vn.edu.usth.recipefoodapp.object_ui.MealSimple;
import vn.edu.usth.recipefoodapp.response.MealDetailResponse;

public class MealDetailActivity extends AppCompatActivity {

    ImageView img;
    TextView title, ingredientsTv, instructionsTv;
    private ImageButton btnFavorite;
    private String mealId;
    private String mealName;
    private String mealImageUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        img = findViewById(R.id.imgMealDetail);
        title = findViewById(R.id.txtMealTitle);
        ingredientsTv = findViewById(R.id.txtIngredients);
        instructionsTv = findViewById(R.id.txtInstructions);
        btnFavorite = findViewById(R.id.buttonFavorite);

        mealId = getIntent().getStringExtra("meal_id");
        mealName = getIntent().getStringExtra("meal_name");
        mealImageUrl = getIntent().getStringExtra("meal_image_url");

        if (mealId == null || mealId.trim().isEmpty()) {
            Toast.makeText(this, "Missing meal id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMealDetail(mealId);

        // check favourite when first come or turn back
        checkIfFavorite();

        // back button
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check and update favourite
        checkIfFavorite();
    }

    private void checkIfFavorite() {
        new Thread(() -> {
            // Check in Room Database exist favourite
            MealSimple existingMeal = DatabaseClient.getInstance(MealDetailActivity.this).getAppDatabase().mealSimpleDao().getFavoriteById(mealId);

            runOnUiThread(() -> {
                if (existingMeal != null) {
                    // change icon if it favourite
                    btnFavorite.setImageResource(R.drawable.favourite_filled_ic);
                } else {
                    // default icon
                    btnFavorite.setImageResource(R.drawable.favourite_icon);
                }
            });
        }).start();
    }


    // fetch data from api
    private void loadMealDetail(String id) {
        ApiClient.getMealApi().getMealDetails(id).enqueue(new Callback<MealDetailResponse>() {
            @Override
            public void onResponse(@NotNull Call<MealDetailResponse> call, @NotNull Response<MealDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().meals != null && !response.body().meals.isEmpty()) {
                    MealDetail m = response.body().meals.get(0);

                    // Update mealName and mealImageUrl after fetch
                    mealName = m.strMeal;
                    mealImageUrl = m.strMealThumb;

                    // Update UI with new data
                    title.setText(m.strMeal);
                    Glide.with(MealDetailActivity.this).load(m.strMealThumb).into(img);
                    instructionsTv.setText(m.strInstructions != null ? m.strInstructions : "");

                    // ingredients
                    List<String> lines = new ArrayList<>();
                    for (int i = 1; i <= 20; i++) {
                        try {
                            String ing = (String) MealDetail.class.getField("strIngredient" + i).get(m);
                            String meas = (String) MealDetail.class.getField("strMeasure" + i).get(m);
                            if (ing != null && !ing.trim().isEmpty()) {
                                lines.add(ing + (meas != null && !meas.trim().isEmpty() ? " - " + meas : ""));
                            }
                        } catch (Exception ignored) {}
                    }
                    StringBuilder sb = new StringBuilder();
                    for (String s : lines) sb.append("• ").append(s).append("\n");
                    ingredientsTv.setText(sb.toString());

                    // Update icon
                    btnFavorite.setOnClickListener(v -> {
                        if (mealName == null || mealImageUrl == null) {
                            Toast.makeText(MealDetailActivity.this, "Can not get the data, Try again", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new Thread(() -> {
                            MealSimple mealSimple = new MealSimple(mealId, mealName, mealImageUrl);
                            MealSimple existingMeal = DatabaseClient.getInstance(MealDetailActivity.this).getAppDatabase().mealSimpleDao().getFavoriteById(mealId);

                            if (existingMeal != null) {
                                // delete meal in room database
                                DatabaseClient.getInstance(MealDetailActivity.this).getAppDatabase().mealSimpleDao().delete(mealSimple);

                                runOnUiThread(() -> {
                                    btnFavorite.setImageResource(R.drawable.favourite_icon);  // update icon to default
                                });
                            } else {
                                // save in room database
                                DatabaseClient.getInstance(MealDetailActivity.this).getAppDatabase().mealSimpleDao().insert(mealSimple);

                                runOnUiThread(() -> {
                                    btnFavorite.setImageResource(R.drawable.favourite_filled_ic);  // Update to red icon favourite
                                });
                            }
                        }).start();
                    });

                } else {
                    Toast.makeText(MealDetailActivity.this, "Can not get the data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<MealDetailResponse> call, @NotNull Throwable t) {
                Toast.makeText(MealDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
