package by.shug.interntrack.repository;

import static by.shug.interntrack.base.Constants.ADDRESS;
import static by.shug.interntrack.base.Constants.COMPANYNAME;
import static by.shug.interntrack.base.Constants.JOBS;
import static by.shug.interntrack.base.Constants.PHONE;
import static by.shug.interntrack.base.Constants.POSITION;
import static by.shug.interntrack.base.Constants.STATUS;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import by.shug.interntrack.repository.model.Job;

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

    public Task<String> getUserStatus() {
        return db.collection(USERS)
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            return document.getString(STATUS);
                        } else {
                            throw new Exception("Пользователь не найден");
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }

    public Task<Void> updateUserData(Map<String, Object> userData) {
        if (auth.getCurrentUser() == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        return db.collection(USERS)
                .document(auth.getCurrentUser().getUid())
                .update(userData);
    }

    public Task<QuerySnapshot> getUsersStudents() {
        return FirebaseFirestore.getInstance()
                .collection(USERS)
                .whereEqualTo(STATUS, "student")
                .get();
    }

    public void saveJob(String companyName, String position, String address, String phone, JobSaveCallback callback) {
        Map<String, String> job = new HashMap<>();
        job.put(COMPANYNAME, companyName);
        job.put(POSITION, position);
        job.put(ADDRESS, address);
        job.put(PHONE, phone);

        db.collection(JOBS)
                .add(job)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Ошибка сохранения вакансии", e);
                    callback.onError(e);
                });
    }

    public void getJobs(JobsCallback callback) {
        db.collection(JOBS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Job> jobList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String companyName = documentSnapshot.getString(COMPANYNAME);
                        String position = documentSnapshot.getString(POSITION);
                        String address = documentSnapshot.getString(ADDRESS);
                        String phone = documentSnapshot.getString(PHONE);

                        jobList.add(new Job(companyName, position, address, phone));
                    }
                    callback.onSuccess(jobList);  // Отправляем результат в callback
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Интерфейс для передачи данных
    public interface JobsCallback {
        void onSuccess(List<Job> jobList);

        void onFailure(String errorMessage);
    }


    public interface JobSaveCallback {
        void onSuccess();

        void onError(Exception e);
    }
}
