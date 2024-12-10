package by.shug.interntrack.ui.grades;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

import by.shug.interntrack.repository.FirebaseRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GradesViewModel  extends ViewModel {
    private final FirebaseRepository repository;

    @Inject
    public GradesViewModel(FirebaseRepository repository) {
        this.repository = repository;
    }

    public void getUserGrades(String userId, OnCompleteListener<DocumentSnapshot> callback) {
        repository.getUserGrades(userId, callback);
    }

    public FirebaseUser getCurrentUser() {
        return repository.getUser();
    }

}
