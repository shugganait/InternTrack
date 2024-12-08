package by.shug.interntrack.repository;

import static by.shug.interntrack.base.Constants.ADDRESS;
import static by.shug.interntrack.base.Constants.COMPANYNAME;
import static by.shug.interntrack.base.Constants.DATE;
import static by.shug.interntrack.base.Constants.FULLNAME;
import static by.shug.interntrack.base.Constants.JOB;
import static by.shug.interntrack.base.Constants.JOBID;
import static by.shug.interntrack.base.Constants.JOBS;
import static by.shug.interntrack.base.Constants.PHONE;
import static by.shug.interntrack.base.Constants.POSITION;
import static by.shug.interntrack.base.Constants.REPORTCONTENT;
import static by.shug.interntrack.base.Constants.REPORTS;
import static by.shug.interntrack.base.Constants.STATUS;
import static by.shug.interntrack.base.Constants.UID;
import static by.shug.interntrack.base.Constants.USERREPORTS;
import static by.shug.interntrack.base.Constants.USERS;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

    //Достает нынешнешнего пользователя в системе
    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

    //Регистрация
    public Task<AuthResult> registerUser(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    //Отправляет ссылку для подтверждения
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return user.sendEmailVerification();
    }

    //Войти
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

    //Выход из аккаунта
    public void logout() {
        auth.signOut();
    }

    //Создает данные пользователя при регистрации
    public void setDataForUser(Map<String, Object> data) {
        db.collection(USERS).document(getUser().getUid())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d("Registration", "User data saved."))
                .addOnFailureListener(e -> Log.e("Registration", "Error saving user data.", e));
    }

    //Достает данные Пользователя
    public Task<DocumentSnapshot> getUserData() {
        return db.collection(USERS)
                .document(getUser().getUid())
                .get();
    }

    //Достает Статус Пользователя (нужен чтобы сверять доступ к функционалу исходя от статуса)
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
                        throw Objects.requireNonNull(task.getException());
                    }
                });
    }

    //Изменяет данные пользователя
    public Task<Void> updateUserData(Map<String, Object> userData) {
        if (auth.getCurrentUser() == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        return db.collection(USERS)
                .document(auth.getCurrentUser().getUid())
                .update(userData);
    }

    //Достает список студентов для админа
    public Task<QuerySnapshot> getUsersStudents() {
        return FirebaseFirestore.getInstance()
                .collection(USERS)
                .whereEqualTo(STATUS, "student")
                .get();
    }

    //Сохраняет вакансии (Для админа)
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

    //Достает список вакансий
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
                        String jobId = documentSnapshot.getId();

                        jobList.add(new Job(companyName, position, address, phone, jobId));
                    }
                    callback.onSuccess(jobList);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //Назначает работу для студента
    public void linkJobToUser(String userId, String jobId, UserUpdateCallback callback) {
        DocumentReference userRef = db.collection(USERS).document(userId);

        userRef.update(JOBID, jobId)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    //Достает одну вакансию по айди
    public void getJobById(String jobId, JobCallback callback) {
        db.collection(JOBS)
                .document(jobId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String companyName = documentSnapshot.getString(COMPANYNAME);
                        String position = documentSnapshot.getString(POSITION);
                        String address = documentSnapshot.getString(ADDRESS);
                        String phoneNumber = documentSnapshot.getString(PHONE);

                        Job job = new Job(companyName, position, address, phoneNumber, jobId);
                        callback.onSuccess(job);
                    } else {
                        callback.onFailure("Job not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //Добавляет отчеты от студента
    public void addUserReport(String userId, String userName, String reportDate, String reportContent, String companyName, String position, OnCompleteListener<Void> callback) {
        db.collection(USERREPORTS).document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                String existingCompanyName = null;

                // Проверка на существование компании
                if (documentSnapshot.contains(JOB)) {
                    Map<String, String> job = (Map<String, String>) documentSnapshot.get(JOB);
                    if (job != null) {
                        existingCompanyName = job.get(COMPANYNAME);
                    }
                }

                // Если название компании не совпадает, создаем новый объект
                if (existingCompanyName == null || !existingCompanyName.equals(companyName)) {
                    createNewUserReport(userId, userName, reportDate, reportContent, companyName, position, callback);
                } else {
                    // Если компании совпадают, обновляем отчеты
                    Map<String, Object> userData = new HashMap<>();
                    userData.put(FULLNAME, userName);
                    userData.put(UID, userId);

                    // Обновляем список отчетов
                    List<Map<String, Object>> reports = (List<Map<String, Object>>) documentSnapshot.get(REPORTS);
                    if (reports == null) {
                        reports = new ArrayList<>();
                    }

                    // Новый отчет
                    Map<String, Object> newReport = new HashMap<>();
                    newReport.put(DATE, reportDate);
                    newReport.put(REPORTCONTENT, reportContent);
                    reports.add(0, newReport); // Добавляем новый отчет в начало списка
                    userData.put(REPORTS, reports);

                    // Вакансия остается неизменной
                    userData.put(JOB, documentSnapshot.get(JOB));

                    // Обновляем документ
                    db.collection(USERREPORTS).document(userId).set(userData).addOnCompleteListener(callback);
                }
            } else {
                // Если объект не существует, создаем новый
                createNewUserReport(userId, userName, reportDate, reportContent, companyName, position, callback);
            }
        });
    }

    // Метод для создания нового объекта с нуля
    private void createNewUserReport(String userId, String userName, String reportDate, String reportContent, String companyName, String position, OnCompleteListener<Void> callback) {
        Map<String, Object> newUserData = new HashMap<>();
        newUserData.put(FULLNAME, userName);
        newUserData.put(UID, userId);

        // Новый список отчетов
        List<Map<String, Object>> newReports = new ArrayList<>();
        Map<String, Object> newReport = new HashMap<>();
        newReport.put(DATE, reportDate);
        newReport.put(REPORTCONTENT, reportContent);
        newReports.add(newReport);
        newUserData.put(REPORTS, newReports);

        // Новая вакансия
        Map<String, String> newJob = new HashMap<>();
        newJob.put(COMPANYNAME, companyName);
        newJob.put(POSITION, position);
        newUserData.put(JOB, newJob);

        db.collection(USERREPORTS).document(userId).set(newUserData).addOnCompleteListener(callback);
    }


    // Интерфейс для передачи данных
    public interface JobCallback {
        void onSuccess(Job job);

        void onFailure(String errorMessage);
    }

    public interface UserUpdateCallback {
        void onSuccess();

        void onFailure(Exception e);
    }

    public interface JobsCallback {
        void onSuccess(List<Job> jobList);

        void onFailure(String errorMessage);
    }

    public interface JobSaveCallback {
        void onSuccess();

        void onError(Exception e);
    }
}
