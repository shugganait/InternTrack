package by.shug.interntrack.ui.dashboard;

import static by.shug.interntrack.base.Constants.EMAIL;
import static by.shug.interntrack.base.Constants.FULLNAME;
import static by.shug.interntrack.base.Constants.GROUP;
import static by.shug.interntrack.base.Constants.PHONE;
import static by.shug.interntrack.base.Constants.STATUS;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import by.shug.interntrack.R;
import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentDashboardBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DashboardFragment extends BaseFragment<FragmentDashboardBinding, DashboardViewModel> {

    private NavController navController;
    private boolean isAuth;
    private String fullName;
    private String email;
    private String phone;
    private String group;
    private String status;

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
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        checkIsAuth();
        setUserData();
        initListener();
    }

    private void setUserData() {
        if (isAuth) {
            if (binding().etLogin.getText().toString().isEmpty()) {
                binding().loadingContainer.setVisibility(View.VISIBLE);
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

                            binding().etLogin.setText(email);
                            binding().etFio.setText(fullName);
                            binding().etGroup.setText(group);
                            binding().etPhone.setText(phone);
                            if (status.equals("admin")) {
                                binding().tvStatus.setText("Админ*");
                            } else {
                                binding().tvStatus.setText("Студент*");
                            }
                            binding().loadingContainer.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("Firestore", "Данные отсутствуют");
                    }
                }).addOnFailureListener(e -> Log.e("Firestore", "Ошибка при получении данных: " + e.getMessage()));
            }
        }
    }

    private void initListener() {
        binding().btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            checkIsAuth();
        });
        binding().btnLogin.setOnClickListener(v -> navController.navigate(R.id.authFragment));
        binding().btnSave.setOnClickListener(v -> updateUserData());
        binding().etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String changedPhone = binding().etPhone.getText().toString();
                if (!Objects.equals(phone, changedPhone)) {
                    binding().btnSave.setVisibility(View.VISIBLE);
                } else {
                    binding().btnSave.setVisibility(View.GONE);
                }
            }
        });
        binding().etGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String changedGroup = binding().etGroup.getText().toString();
                if (!Objects.equals(group, changedGroup)) {
                    binding().btnSave.setVisibility(View.VISIBLE);
                } else {
                    binding().btnSave.setVisibility(View.GONE);
                }
            }
        });
        binding().etFio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String changedFullName = binding().etFio.getText().toString();
                if (!Objects.equals(fullName, changedFullName)) {
                    binding().btnSave.setVisibility(View.VISIBLE);
                } else {
                    binding().btnSave.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateUserData() {
        closeKeyboard(requireActivity());
        binding().loadingContainer.setVisibility(View.VISIBLE);
        String newPhone = binding().etPhone.getText().toString();
        String newName = binding().etFio.getText().toString();
        String newGroup = binding().etGroup.getText().toString();

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
                binding().loadingContainer.setVisibility(View.GONE);
                binding().btnSave.setVisibility(View.GONE);
            });
        }
    }

    private void checkIsAuth() {
        if (viewModel.getCurrentUser() != null && viewModel.getCurrentUser().isEmailVerified()) {
            isAuth = true;
            binding().loginSuggest.setVisibility(View.GONE);
        } else {
            isAuth = false;
            binding().loginSuggest.setVisibility(View.VISIBLE);

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