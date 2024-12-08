package by.shug.interntrack.ui.reports;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import by.shug.interntrack.repository.FirebaseRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReportsViewModel extends ViewModel {
    private final FirebaseRepository repository;

    @Inject
    public ReportsViewModel(FirebaseRepository repository) {
        this.repository = repository;
    }

    private void test() {
        repository.getUsersStudents();
    }


}
