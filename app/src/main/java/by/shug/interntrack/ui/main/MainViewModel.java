package by.shug.interntrack.ui.main;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

import by.shug.interntrack.repository.FirebaseRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final FirebaseRepository repository;

    @Inject
    public MainViewModel(FirebaseRepository repository) {
        this.repository = repository;
    }

    public Task<String> getUserStatus() {
        if (repository.getUser() != null) {
            return repository.getUserStatus();
        } else {
            return Tasks.forException(new Exception("Пользователь не авторизован"));
        }
    }

    public FirebaseUser getCurrentUser() {
        return repository.getUser();
    }

    public Task<QuerySnapshot> getUsersStudents() {
        return repository.getUsersStudents();
    }

    public void getJobs(FirebaseRepository.JobsCallback callback) {
        repository.getJobs(callback);
    }
}