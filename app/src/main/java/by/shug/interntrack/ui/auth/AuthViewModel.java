package by.shug.interntrack.ui.auth;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import javax.inject.Inject;

import by.shug.interntrack.repository.FirebaseRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final FirebaseRepository repository;

    @Inject
    public AuthViewModel(FirebaseRepository repository) {
        this.repository = repository;
    }

    public void registerUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        repository.registerUser(email, password).addOnCompleteListener(listener);
    }

    public Task<AuthResult> loginUser(String email, String password) {
        return repository.loginUser(email, password);
    }


    public FirebaseUser getCurrentUser() {
        return repository.getUser();
    }

    public void sendVerificationEmail(FirebaseUser user, OnCompleteListener<Void> listener) {
        repository.sendVerificationEmail(user).addOnCompleteListener(listener);
    }

    public void setDataToUser(Map<String, Object> data) {
        repository.setDataForUser(data);
    }

}
