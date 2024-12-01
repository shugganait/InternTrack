package by.shug.interntrack;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import by.shug.interntrack.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_main, R.id.navigation_dashboard)
//                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //Слушатель чтобы BottomNavigation скрывался при дпо экранах
        bottomVisibilityListener(navController, navView);

        // Начальный фрагмент
        navController.navigate(R.id.loginFragment);
    }

    private void bottomVisibilityListener(NavController navController, BottomNavigationView navView) {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (isBottomNavFragment(destination.getId())) {
                navView.setVisibility(View.VISIBLE);
            } else {
                navView.setVisibility(View.GONE);
            }
        });
    }

    // Метод для проверки, является ли фрагмент частью BottomNavigation
    private boolean isBottomNavFragment(int destinationId) {
        return destinationId == R.id.navigation_main || destinationId == R.id.navigation_dashboard;
    }
}