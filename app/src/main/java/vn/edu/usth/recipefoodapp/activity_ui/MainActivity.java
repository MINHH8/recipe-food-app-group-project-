package vn.edu.usth.recipefoodapp.activity_ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.edu.usth.recipefoodapp.R;
import vn.edu.usth.recipefoodapp.fragment_ui.CategoryFragment;
import vn.edu.usth.recipefoodapp.fragment_ui.FavouriteFragment;
import vn.edu.usth.recipefoodapp.fragment_ui.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private TextView dynamicTitle;
    private SharedPreferences prefs;

    public static final String PREFS_NAME = "RecipeAppPrefs";
    public static final String KEY_DARK_MODE = "DarkModeEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Set the theme BEFORE the UI is created
        if (prefs.getBoolean(KEY_DARK_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dynamicTitle = findViewById(R.id.dynamic_title);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
            dynamicTitle.setText(R.string.title_home);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            String title = "";
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selected = new HomeFragment();
                title = getString(R.string.title_home);
            } else if (id == R.id.nav_category) {
                selected = new CategoryFragment();
                title = getString(R.string.title_category);
            } else if (id == R.id.nav_favorite) {
                selected = new FavouriteFragment();
                title = getString(R.string.title_favorite);
            }
            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selected)
                        .commit();
                dynamicTitle.setText(title);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    // THIS IS THE FIX FOR THE ICONS
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            // This code uses reflection to force icons to be shown in the overflow menu.
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    java.lang.reflect.Method method = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    // Log the error or handle it gracefully
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem darkModeItem = menu.findItem(R.id.action_toggle_dark_mode);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);

        if (isDarkMode) {
            darkModeItem.setTitle("Light Mode");
            darkModeItem.setIcon(R.drawable.dark_mode);
        } else {
            darkModeItem.setTitle("Dark Mode");
            darkModeItem.setIcon(R.drawable.dark_mode);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_toggle_dark_mode) {
            boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
            prefs.edit().putBoolean(KEY_DARK_MODE, !isDarkMode).apply();
            AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                    AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}