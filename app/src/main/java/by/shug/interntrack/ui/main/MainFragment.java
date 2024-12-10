package by.shug.interntrack.ui.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import by.shug.interntrack.R;
import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentMainBinding;
import by.shug.interntrack.repository.FirebaseRepository;
import by.shug.interntrack.repository.model.Job;
import by.shug.interntrack.repository.model.User;
import by.shug.interntrack.ui.main.adapter.JobsAdapter;
import by.shug.interntrack.ui.main.adapter.OnJobClickListener;
import by.shug.interntrack.ui.main.adapter.OnUserClickListener;
import by.shug.interntrack.ui.main.adapter.UserAdapter;
import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class MainFragment extends BaseFragment<FragmentMainBinding, MainViewModel> implements OnUserClickListener, OnJobClickListener {

    public static String MAIN_UID = "uid";
    private NavController navController;
    private UserAdapter userAdapter;
    private JobsAdapter jobsAdapter;
    private boolean isAdmin;
    private boolean isChoosingJob = false;
    private String userForJob;

    @Override
    protected FragmentMainBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected Class<MainViewModel> getViewModelClass() {
        return MainViewModel.class;
    }

    @Override
    protected void uiBox() {
        navController = Navigation.findNavController(getBinding.getRoot());
        initAdapter();
        checkIsAuth();
        loadUserStatus();
        initListeners();
    }

    private void initAdapter() {
        getBinding.rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        getBinding.rvVacancy.setLayoutManager(new LinearLayoutManager(requireContext()));

        userAdapter = new UserAdapter(this);
        getBinding.rvStudents.setAdapter(userAdapter);
        jobsAdapter = new JobsAdapter(this);
        getBinding.rvVacancy.setAdapter(jobsAdapter);
    }

    private void initListeners() {
        getBinding.btnLogin.setOnClickListener(v -> navController.navigate(R.id.authFragment));
        getBinding.fab.setOnClickListener(v -> {
            if (isAdmin) {
                navController.navigate(R.id.saveJobFragment);
            }
        });
        getBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == getBinding.rbStudents.getId() && isAdmin) {
                getBinding.rvStudents.setVisibility(View.VISIBLE);
                getBinding.rvVacancy.setVisibility(View.GONE);
                getBinding.loadingContainer.setVisibility(View.VISIBLE);
                getUsersStudents();
                getBinding.fab.setVisibility(View.GONE);
                getBinding.btnCancel.setVisibility(View.GONE);
            } else if (checkedId == getBinding.rbVacancy.getId() && isAdmin) {
                getBinding.rvStudents.setVisibility(View.GONE);
                getBinding.rvVacancy.setVisibility(View.VISIBLE);
                getBinding.loadingContainer.setVisibility(View.VISIBLE);
                if (isChoosingJob) {
                    getBinding.fab.setVisibility(View.GONE);
                    getBinding.btnCancel.setVisibility(View.VISIBLE);
                } else {
                    getBinding.fab.setVisibility(View.VISIBLE);
                    getBinding.btnCancel.setVisibility(View.GONE);
                }
                getJobs();
            }
        });
        getBinding.btnCancel.setOnClickListener(v -> {
            isChoosingJob = false;
            getBinding.btnCancel.setVisibility(View.GONE);
            getBinding.fab.setVisibility(View.VISIBLE);
        });
    }

    private void checkIsAuth() {
        if (viewModel.getCurrentUser() != null && viewModel.getCurrentUser().isEmailVerified()) {
            getBinding.loginSuggest.setVisibility(View.GONE);
        } else {
            getBinding.loginSuggest.setVisibility(View.VISIBLE);
        }
    }

    private void loadUserStatus() {
        getBinding.loadingContainer.setVisibility(View.VISIBLE);
        viewModel.getUserStatus()
                .addOnSuccessListener(status -> {
                    getBinding.loadingContainer.setVisibility(View.GONE);
                    isAdmin = status.equals("admin");
                    if (isAdmin) {
                        getBinding.rbStudents.setChecked(true);
                        getBinding.radioGroup.setVisibility(View.VISIBLE);
                        getBinding.rbStudents.setVisibility(View.VISIBLE);
                    } else {
                        getBinding.rbVacancy.setChecked(true);
                        getBinding.radioGroup.setVisibility(View.VISIBLE);
                        getBinding.rbStudents.setVisibility(View.GONE);
                        getBinding.rvVacancy.setVisibility(View.VISIBLE);
                        getBinding.loadingContainer.setVisibility(View.VISIBLE);
                        getJobs();
                    }
                })
                .addOnFailureListener(e -> {
                    getBinding.loadingContainer.setVisibility(View.GONE);
                    Log.e("UserStatus", "Ошибка получения статуса", e);
                });
    }

    private void getUsersStudents() {
        viewModel.getUsersStudents()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        List<User> users = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            User user = document.toObject(User.class);
                            users.add(user);
                        }
                        Log.d("UsersByStatus", "Найдено пользователей: " + users.size());
                        userAdapter.setUserList(users);
                        getBinding.loadingContainer.setVisibility(View.GONE);
                    } else {
                        getBinding.loadingContainer.setVisibility(View.GONE);
                        Log.d("UsersByStatus", "Нет пользователей со статусом: СТУЛЕНТ");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UsersByStatus", "Ошибка загрузки пользователей", e);
                    getBinding.loadingContainer.setVisibility(View.GONE);
                });
    }

    private void getJobs() {
        viewModel.getJobs(new FirebaseRepository.JobsCallback() {
            @Override
            public void onSuccess(List<Job> jobList) {
                jobsAdapter.setJobsList(jobList);
                getBinding.loadingContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("shug", "Error: " + errorMessage);
                getBinding.loadingContainer.setVisibility(View.GONE);
            }
        });
    }

    private void linkJobToUser(String userID, String jobId) {
        viewModel.linkJobToUser(userID, jobId, new FirebaseRepository.UserUpdateCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "Пользователю назначена работа", Toast.LENGTH_SHORT).show();
                getBinding.btnCancel.setVisibility(View.GONE);
                getBinding.fab.setVisibility(View.VISIBLE);
                isChoosingJob = false;
                getBinding.loadingContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("shug", "Failed to link job: " + e.getMessage());
                getBinding.loadingContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onUserClick(User data) {
        if (isAdmin) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Назначить работу к \"" + data.getFullName() + "\"?")
                    .setCancelable(false)
                    .setPositiveButton("Принять", (dialog, id) -> {
                        isChoosingJob = true;
                        getBinding.rbVacancy.setChecked(true);
                        userForJob = data.getUid();
                    })
                    .setNegativeButton("Отмена", (dialog, id) -> {
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onUserLongClick(User data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Посмотреть отчеты \"" + data.getFullName() + "\"?")
                .setCancelable(false)
                .setPositiveButton("Да", (dialog, id) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(MAIN_UID, data.getUid());
                    navController.navigate(R.id.reportsFragment, bundle);
                    })
                .setNegativeButton("Отмена", (dialog, id) -> {
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onJobClick(Job data) {
        if (isChoosingJob) {
            getBinding.loadingContainer.setVisibility(View.VISIBLE);
            getBinding.btnCancel.setVisibility(View.GONE);
            linkJobToUser(userForJob, data.getJobID());
        }
    }
}