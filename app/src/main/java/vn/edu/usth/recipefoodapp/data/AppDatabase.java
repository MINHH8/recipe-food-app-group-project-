package vn.edu.usth.recipefoodapp.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import vn.edu.usth.recipefoodapp.object_ui.MealSimple;

@Database(entities = {MealSimple.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MealSimpleDao mealSimpleDao();
}
