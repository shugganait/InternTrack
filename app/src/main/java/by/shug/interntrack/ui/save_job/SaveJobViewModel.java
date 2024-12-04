package by.shug.interntrack.ui.save_job;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import by.shug.interntrack.repository.FirebaseRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SaveJobViewModel extends ViewModel {
    private final FirebaseRepository repository;

    @Inject
    public SaveJobViewModel(FirebaseRepository repository) {
        this.repository = repository;
    }

    public void saveJob(String companyName, String position, String address, String phone, FirebaseRepository.JobSaveCallback callback) {
        repository.saveJob(companyName, position, address, phone, callback);
    }
}
