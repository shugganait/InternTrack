package by.shug.interntrack.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import by.shug.interntrack.R;
import by.shug.interntrack.databinding.ActivityMainBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //Слушатель чтобы BottomNavigation скрывался при доп экранах
        bottomVisibilityListener(navController, binding.navView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            navController.navigate(R.id.authFragment);
        }
        if ( user != null && !user.isEmailVerified()) {
            navController.navigate(R.id.authFragment);
        }
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