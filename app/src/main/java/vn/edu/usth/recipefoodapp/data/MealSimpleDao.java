package vn.edu.usth.recipefoodapp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import vn.edu.usth.recipefoodapp.object_ui.MealSimple;

@Dao
public interface MealSimpleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MealSimple mealSimple);

    @Delete
    void delete(MealSimple mealSimple);

    @Query("SELECT * FROM favorite_meals WHERE IdMeal = :mealId LIMIT 1")
    MealSimple getFavoriteById(String mealId);

    @Query("SELECT * FROM favorite_meals")
    List<MealSimple> getAllFavorites();
}


