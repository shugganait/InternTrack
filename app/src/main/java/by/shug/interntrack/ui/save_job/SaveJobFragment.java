package by.shug.interntrack.ui.save_job;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentSaveJobBinding;
import by.shug.interntrack.repository.FirebaseRepository;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SaveJobFragment extends BaseFragment<FragmentSaveJobBinding, SaveJobViewModel> {

    private NavController navController;

    @Override
    protected FragmentSaveJobBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSaveJobBinding.inflate(getLayoutInflater());
    }

    @Override
    protected Class<SaveJobViewModel> getViewModelClass() {
        return SaveJobViewModel.class;
    }

    @Override
    protected void uiBox() {
        navController = Navigation.findNavController(getBinding.getRoot());
        getBinding.btnOk.setOnClickListener(v -> {
            validateAndSave();
        });
    }

    private void validateAndSave() {
        String name = getBinding.etName.getText().toString();
        String position = getBinding.etPosition.getText().toString();
        String phoneNumber = getBinding.etPhone.getText().toString();
        String address = getBinding.etAddress.getText().toString();

        if (!name.isEmpty() && !position.isEmpty() && !phoneNumber.isEmpty() && !address.isEmpty()) {
            viewModel.saveJob(name, position, address, phoneNumber, new FirebaseRepository.JobSaveCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(requireContext(), "Вакансия сохранена!", Toast.LENGTH_SHORT).show();
                    navController.navigateUp();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else showToast("Заполните все данные!");
    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}