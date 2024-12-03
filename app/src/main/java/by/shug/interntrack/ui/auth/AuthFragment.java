package by.shug.interntrack.ui.auth;

import static by.shug.interntrack.base.Constants.EMAIL;
import static by.shug.interntrack.base.Constants.FULLNAME;
import static by.shug.interntrack.base.Constants.GROUP;
import static by.shug.interntrack.base.Constants.PHONE;
import static by.shug.interntrack.base.Constants.STATUS;
import static by.shug.interntrack.base.Constants.UID;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

import by.shug.interntrack.R;
import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentAuthBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthFragment extends BaseFragment<FragmentAuthBinding, AuthViewModel> {

    private boolean isAdmin = false;
    private boolean isReg = false;
    private NavController navController;

    @Override
    protected FragmentAuthBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAuthBinding.inflate(getLayoutInflater());
    }

    @Override
    protected Class<AuthViewModel> getViewModelClass() {
        return AuthViewModel.class;
    }

    @Override
    protected void uiBox() {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        initListeners();
    }

    private void initListeners() {
        binding().tvRgOrLog.setOnClickListener(v -> setVisibleRegOrLog());
        binding().tvChangeStatusReg.setOnClickListener(v -> setStatus());
        binding().btnNext.setOnClickListener(v -> validateRegOrLog());
    }

    public void validateRegOrLog() {
        String email = binding().etLoginReg.getText().toString();
        String fullName = binding().etFio.getText().toString();
        String passwordReg = binding().etPasswordReg.getText().toString();
        String repPasswordReg = binding().etRepPasswordReg.getText().toString();
        String groupOrDepartment = binding().etGroup.getText().toString();
        String phone = binding().etPhone.getText().toString();
        //
        String emailLog = binding().etLogin.getText().toString();
        String passwordLog = binding().etPassword.getText().toString();
        if (isReg) {
            /////REGISTRATION
            if (!email.isEmpty() && !fullName.isEmpty() && !passwordReg.isEmpty()
                    && !repPasswordReg.isEmpty() && !groupOrDepartment.isEmpty() && !phone.isEmpty()) {
                if (passwordReg.length() >= 6) {
                    if (passwordReg.equals(repPasswordReg)) {
                        register(email, passwordReg, fullName, phone, groupOrDepartment);
                    } else showToast("Пароли не совпадают");
                } else showToast("Пароль должен быть больше 6 символов");
            } else showToast("Заполните все данные");
        } else {
            //LOGIN
            if (!emailLog.isEmpty() && !passwordLog.isEmpty()) {
                login(emailLog, passwordLog);
            } else showToast("Введите email и пароль.");
        }
    }

    private void register(String email, String password, String fullName, String phone, String
            group) {
        viewModel.registerUser(email, password, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = viewModel.getCurrentUser();
                // Отправить email для верификации
                viewModel.sendVerificationEmail(user, emailTask -> {
                    if (emailTask.isSuccessful()) {
                        if (user != null) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put(FULLNAME, fullName);
                            userData.put(PHONE, phone);
                            userData.put(EMAIL, email);
                            userData.put(GROUP, group);
                            if (isAdmin) {
                                userData.put(STATUS, "admin");
                            } else {
                                userData.put(STATUS, "student");
                            }
                            userData.put(UID, user.getUid());

                            viewModel.setDataToUser(userData);
                        }
                        openVerSentContainer();
                    } else {
                        showToast("Не удалось отправить ссылку подтверждения");
                    }
                });
            } else {
                Exception exception = task.getException();
                if (exception instanceof FirebaseAuthException) {
                    FirebaseAuthException authException = (FirebaseAuthException) exception;
                    String errorCode = authException.getErrorCode();
                    handleError(errorCode);
                } else {
                    if (exception != null) {
                        showToast("Ошибка: " + exception.getMessage());
                    }
                }
            }
        });
    }

    private void openVerSentContainer() {
        binding().waitContainer.setVisibility(View.VISIBLE);
        binding().btnNext.setVisibility(View.GONE);
        binding().btnOk.setOnClickListener(v -> {
            binding().waitContainer.setVisibility(View.GONE);
            binding().btnNext.setVisibility(View.VISIBLE);
            binding().etLogin.setText(binding().etLoginReg.getText().toString());
            binding().etPassword.setText(binding().etPasswordReg.getText().toString());
            setVisibleRegOrLog();
        });
    }

    private void login(String email, String password) {
        viewModel.loginUser(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (viewModel.getCurrentUser() != null && viewModel.getCurrentUser().isEmailVerified()) {
                            // Навигация после успешного входа
                            showToast("Вход выполнен успешно!");
                            navController.navigateUp();
                        } else if (viewModel.getCurrentUser() != null) {
                            showToast("Почта не подтверждена. Проверьте свою почту.");
                        } else {
                            showToast("Ошибка: Пользователь не найден.");
                        }
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            showToast("Неверный пароль. Попробуйте снова.");
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            showToast("Пользователь не найден. Проверьте данные.");
                        } else if (e != null) {
                            showToast("Ошибка входа: " + e.getMessage());
                        }
                    }
                });
    }

    private void setStatus() {
        if (!isAdmin) {
            isAdmin = true;
            binding().etGroup.setHint("Кафедра");
            binding().tvChangeStatusReg.setText(R.string.reg_as_student);
        } else {
            isAdmin = false;
            binding().etGroup.setHint("Группа");
            binding().tvChangeStatusReg.setText(R.string.reg_as_admin);

        }
    }

    private void setVisibleRegOrLog() {
        if (binding().containerReg.getVisibility() == View.VISIBLE) {
            isReg = false;
            binding().containerLogin.setVisibility(View.VISIBLE);
            binding().containerReg.setVisibility(View.GONE);
            binding().tvRgOrLog.setText(R.string.not_have_an_account);
            binding().tvTitle.setText(R.string.log_in_acc);
        } else {
            isReg = true;
            binding().containerLogin.setVisibility(View.GONE);
            binding().containerReg.setVisibility(View.VISIBLE);
            binding().tvRgOrLog.setText(R.string.got_account);
            binding().tvTitle.setText(R.string.create_acc);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void handleError(String errorCode) {
        switch (errorCode) {
            case "ERROR_EMAIL_ALREADY_IN_USE":
                showToast("Электронная почта уже используется.");
                break;
            case "ERROR_INVALID_EMAIL":
                showToast("Некорректный формат электронной почты.");
                break;
            case "ERROR_WEAK_PASSWORD":
                showToast("Пароль слишком слабый.");
                break;
            case "ERROR_USER_DISABLED":
                showToast("Этот пользователь заблокирован.");
                break;
            case "ERROR_USER_NOT_FOUND":
                showToast("Пользователь не найден.");
                break;
            case "ERROR_WRONG_PASSWORD":
                showToast("Неверный пароль.");
                break;
            case "ERROR_OPERATION_NOT_ALLOWED":
                showToast("Операция не разрешена.");
                break;
            default:
                showToast("Произошла ошибка: " + errorCode);
                break;
        }
    }

}