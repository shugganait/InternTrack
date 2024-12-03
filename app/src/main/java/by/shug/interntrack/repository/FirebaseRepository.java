package by.shug.interntrack.repository;

import static by.shug.interntrack.base.Constants.USERS;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirebaseRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    @Inject
    public FirebaseRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

    public Task<AuthResult> registerUser(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return user.sendEmailVerification();
    }

    public Task<AuthResult> loginUser(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("shug", "Вход выполнен успешно.");
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.e("shug", "Ошибка входа: Неверный пароль.");
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            Log.e("shug", "Ошибка входа: Пользователь не найден.");
                        } else if (e != null) {
                            Log.e("shug", "Ошибка входа: " + e.getMessage());
                        }
                    }
                });
    }

    public void logout() {
        auth.signOut();
    }

    public void setDataForUser(Map<String, Object> data) {
        db.collection(USERS).document(getUser().getUid())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d("Registration", "User data saved."))
                .addOnFailureListener(e -> Log.e("Registration", "Error saving user data.", e));
    }

    public Task<DocumentSnapshot> getUserData() {
        return db.collection(USERS)
                .document(getUser().getUid())
                .get();
    }

    public Task<Void> updateUserData(Map<String, Object> userData) {
        if (auth.getCurrentUser() == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        return db.collection(USERS)
                .document(auth.getCurrentUser().getUid())
                .update(userData);
    }
}
