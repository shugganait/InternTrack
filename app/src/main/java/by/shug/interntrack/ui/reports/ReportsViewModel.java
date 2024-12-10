package by.shug.interntrack.ui.reports;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

import by.shug.interntrack.repository.FirebaseRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReportsViewModel extends ViewModel {
    private final FirebaseRepository repository;

    @Inject
    public ReportsViewModel(FirebaseRepository repository) {
        this.repository = repository;
    }

    public void getUserReports(String userId, OnCompleteListener<DocumentSnapshot> callback) {
        repository.getUserReports(userId, callback);
    }

    public void addUserGrade(String userId,String name, String companyName, String position, String grade, OnCompleteListener<Void> callback) {
        repository.addUserGrade(userId, name, companyName, position, grade, callback);
    }
}
