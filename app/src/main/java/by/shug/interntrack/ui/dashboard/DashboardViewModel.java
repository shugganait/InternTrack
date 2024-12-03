package by.shug.interntrack.ui.dashboard;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

import javax.inject.Inject;

import by.shug.interntrack.repository.FirebaseRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DashboardViewModel extends ViewModel {
    private final FirebaseRepository repository;

    @Inject
    public DashboardViewModel(FirebaseRepository repository) {
        this.repository = repository;
    }

    public void logout() {
        repository.logout();
    }

    public FirebaseUser getCurrentUser() {
        return repository.getUser();
    }

    public Task<DocumentSnapshot> getUserData(){
        return repository.getUserData();
    }

    public Task<Void> updateUser(Map<String, Object> userData) {
        return repository.updateUserData(userData);
    }

}
