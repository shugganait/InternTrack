package by.shug.interntrack.ui.dashboard;

import static by.shug.interntrack.base.Constants.EMAIL;
import static by.shug.interntrack.base.Constants.FULLNAME;
import static by.shug.interntrack.base.Constants.GROUP;
import static by.shug.interntrack.base.Constants.JOBID;
import static by.shug.interntrack.base.Constants.PHONE;
import static by.shug.interntrack.base.Constants.STATUS;
import static by.shug.interntrack.base.Constants.UID;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import by.shug.interntrack.R;
import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentDashboardBinding;
import by.shug.interntrack.repository.FirebaseRepository;
import by.shug.interntrack.repository.model.Job;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DashboardFragment extends BaseFragment<FragmentDashboardBinding, DashboardViewModel> {

    private NavController navController;
    private boolean isAuth;
    private boolean isAdmin;
    private String fullName;
    private String uid;
    private String email;
    private String phone;
    private String group;
    private String status;
    private String jobId = "null";

    @Override
    protected FragmentDashboardBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDashboardBinding.inflate(getLayoutInflater());
    }

    @Override
    protected Class<DashboardViewModel> getViewModelClass() {
        return DashboardViewModel.class;
    }

    @Override
    protected void uiBox() {
        navController = Navigation.findNavController(getBinding.getRoot());
        checkIsAuth();
        setUserData();
        initListener();
    }

    private void setUserData() {
        if (isAuth) {
            if (getBinding.etLogin.getText().toString().isEmpty()) {
                getBinding.loadingContainer.setVisibility(View.VISIBLE);
                viewModel.getUserData().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Данные найдены
                        Map<String, Object> userData = documentSnapshot.getData();
                        if (userData != null) {
                            fullName = (String) userData.get(FULLNAME);
                            email = (String) userData.get(EMAIL);
                            phone = (String) userData.get(PHONE);
                            group = (String) userData.get(GROUP);
                            status = (String) userData.get(STATUS);
                            if (userData.get(JOBID) != null) {
                                jobId = (String) userData.get(JOBID);
                            }
                            uid = (String) userData.get(UID);

                            getBinding.etLogin.setText(email);
                            getBinding.etFio.setText(fullName);
                            getBinding.etGroup.setText(group);
                            getBinding.etPhone.setText(phone);
                            if (status.equals("admin")) {
                                getBinding.tvStatus.setText("Админ*");
                                isAdmin = true;
                            } else {
                                getBinding.tvStatus.setText("Студент*");
                                getBinding.btnGrades.setVisibility(View.VISIBLE);
                                isAdmin = false;
                            }
                        }

                        if (!Objects.equals(jobId, "null")) {
                            setJobIfExists();
                        } else {
                            getBinding.loadingContainer.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("Firestore", "Данные отсутствуют");
                    }
                }).addOnFailureListener(e -> Log.e("Firestore", "Ошибка при получении данных: " + e.getMessage()));
            }
        }
    }

    private void initListener() {
        getBinding.btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            checkIsAuth();
        });
        getBinding.btnLogin.setOnClickListener(v -> {
            navController.navigate(R.id.authFragment);
            jobId = "null";
        });
        getBinding.btnSave.setOnClickListener(v -> updateUserData());
        getBinding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String changedPhone = getBinding.etPhone.getText().toString();
                if (!Objects.equals(phone, changedPhone)) {
                    if (getBinding.loadingContainer.getVisibility() == View.GONE) {
                        getBinding.btnSave.setVisibility(View.VISIBLE);
                    }
                } else {
                    getBinding.btnSave.setVisibility(View.GONE);
                }
            }
        });
        getBinding.etGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String changedGroup = getBinding.etGroup.getText().toString();
                if (!Objects.equals(group, changedGroup)) {
                    if (getBinding.loadingContainer.getVisibility() == View.GONE) {
                        getBinding.btnSave.setVisibility(View.VISIBLE);
                    }
                } else {
                    getBinding.btnSave.setVisibility(View.GONE);
                }
            }
        });
        getBinding.etFio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String changedFullName = getBinding.etFio.getText().toString();
                if (!Objects.equals(fullName, changedFullName)) {
                    if (getBinding.loadingContainer.getVisibility() == View.GONE) {
                        getBinding.btnSave.setVisibility(View.VISIBLE);
                    }
                } else {
                    getBinding.btnSave.setVisibility(View.GONE);
                }
            }
        });
        getBinding.etReport.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String changedReport = getBinding.etReport.getText().toString();
                if (!changedReport.isEmpty()) {
                    getBinding.btnSendReport.setVisibility(View.VISIBLE);
                } else {
                    getBinding.btnSendReport.setVisibility(View.GONE);
                }
            }
        });
        getBinding.btnSendReport.setOnClickListener(v -> {
            getBinding.loadingContainer.setVisibility(View.VISIBLE);
            getBinding.btnSave.setVisibility(View.GONE);
            sendReport();
        });
        getBinding.btnGrades.setOnClickListener(v -> navController.navigate(R.id.gradesFragment));
    }

    private void setJobIfExists() {
        viewModel.fetchJobById(jobId, new FirebaseRepository.JobCallback() {
            @Override
            public void onSuccess(Job job) {
                getBinding.tvJob.setVisibility(View.VISIBLE);
                getBinding.jobContainer.setVisibility(View.VISIBLE);
                getBinding.etReport.setVisibility(View.VISIBLE);
                getBinding.tvName.setText(job.getCompanyName());
                getBinding.tvPosition.setText(job.getPosition());
                getBinding.tvAddress.setText(job.getAddress());
                getBinding.tvPhone.setText(job.getPhoneNumber());
                Toast.makeText(requireContext(), job.getCompanyName(), Toast.LENGTH_SHORT).show();
                getBinding.loadingContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdmin) {
                    getBinding.tvJob.setText(R.string.nochosen_job);
                    getBinding.tvJob.setVisibility(View.VISIBLE);
                }
                getBinding.loadingContainer.setVisibility(View.GONE);
            }
        });
    }

    private void sendReport() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = (day + "/" + month + "/" + year);
        String reportContent = getBinding.etReport.getText().toString();
        String companyName = getBinding.tvName.getText().toString();
        String position = getBinding.tvPosition.getText().toString();

        viewModel.addUserReport(uid, fullName, currentDate, reportContent, companyName, position, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Отчет отправлен", Toast.LENGTH_SHORT).show();
                getBinding.etReport.setText("");
                getBinding.loadingContainer.setVisibility(View.GONE);
            } else {
                getBinding.loadingContainer.setVisibility(View.GONE);
                Log.e("shug", "Failed to add report: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void updateUserData() {
        closeKeyboard(requireActivity());
        getBinding.loadingContainer.setVisibility(View.VISIBLE);
        String newPhone = getBinding.etPhone.getText().toString();
        String newName = getBinding.etFio.getText().toString();
        String newGroup = getBinding.etGroup.getText().toString();

        phone = newPhone;
        fullName = newName;
        group = newGroup;

        if (!newName.isEmpty() && !newPhone.isEmpty() && !newGroup.isEmpty()) {
            Map<String, Object> userData = new HashMap<>();
            userData.put(FULLNAME, newName);
            userData.put(PHONE, newPhone);
            userData.put(GROUP, newGroup);

            viewModel.updateUser(userData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("Данные успешно обновлены");
                } else {
                    showToast("Ошибка при обновлении данных, попробуйте позже");
                }
                getBinding.loadingContainer.setVisibility(View.GONE);
                getBinding.btnSave.setVisibility(View.GONE);
            });
        }
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

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void closeKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

}