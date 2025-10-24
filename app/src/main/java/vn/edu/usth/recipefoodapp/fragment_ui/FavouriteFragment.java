package vn.edu.usth.recipefoodapp.fragment_ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.recipefoodapp.R;
import vn.edu.usth.recipefoodapp.adapter_ui.MealAdapter;
import vn.edu.usth.recipefoodapp.data.DatabaseClient;
import vn.edu.usth.recipefoodapp.object_ui.MealSimple;

public class FavouriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<MealSimple> mealList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // get favourite list from Room Database
        mealList = new ArrayList<>();
        mealAdapter = new MealAdapter(getContext(), mealList);
        recyclerView.setAdapter(mealAdapter);

        // load favourite data
        loadFavoriteMeals();

        return rootView;
    }

    // method to load data from Room Database
    private void loadFavoriteMeals() {
        new Thread(() -> {
            List<MealSimple> fetchedFavorites = DatabaseClient.getInstance(getContext()).getAppDatabase().mealSimpleDao().getAllFavorites();

            // update favourite lists
            getActivity().runOnUiThread(() -> {
                mealList.clear();
                mealList.addAll(fetchedFavorites);
                mealAdapter.notifyDataSetChanged();  // update RecyclerView
            });
        }).start();
    }

    // method to add and remove from favourite list
    public void updateFavorites() {
        loadFavoriteMeals();
    }

    @Override
    public void onResume() {
        super.onResume();
        // update favourite list when turn back fragment
        updateFavorites();
    }
}
