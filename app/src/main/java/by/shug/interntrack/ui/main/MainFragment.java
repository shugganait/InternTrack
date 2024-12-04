package by.shug.interntrack.ui.main;

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
import by.shug.interntrack.ui.main.adapter.UserAdapter;
import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class MainFragment extends BaseFragment<FragmentMainBinding, MainViewModel> {

    private NavController navController;
    private UserAdapter userAdapter;
    private JobsAdapter jobsAdapter;
    private boolean isAdmin;
    private boolean isAuth;

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

        userAdapter = new UserAdapter(new ArrayList<>());
        getBinding.rvStudents.setAdapter(userAdapter);
        jobsAdapter = new JobsAdapter(new ArrayList<>());
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
            } else if (checkedId == getBinding.rbVacancy.getId() && isAdmin) {
                getBinding.rvStudents.setVisibility(View.GONE);
                getBinding.rvVacancy.setVisibility(View.VISIBLE);
                getBinding.loadingContainer.setVisibility(View.VISIBLE);
                getJobs();
            }
        });
    }

    private void checkIsAuth() {
        if (viewModel.getCurrentUser() != null && viewModel.getCurrentUser().isEmailVerified()) {
            isAuth = true;
            getBinding.loginSuggest.setVisibility(View.GONE);
        } else {
            isAuth = false;
            getBinding.loginSuggest.setVisibility(View.VISIBLE);
        }
    }

    private void loadUserStatus() {
        getBinding.loadingContainer.setVisibility(View.VISIBLE);
        viewModel.getUserStatus()
                .addOnSuccessListener(status -> {
                    getBinding.loadingContainer.setVisibility(View.GONE);
                    showToast("Статус: " + status);
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
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                getBinding.loadingContainer.setVisibility(View.GONE);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}