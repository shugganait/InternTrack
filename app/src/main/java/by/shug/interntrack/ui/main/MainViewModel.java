package by.shug.interntrack.ui.main;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import by.shug.interntrack.repository.FirebaseRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final FirebaseRepository repository;

    @Inject
    public MainViewModel(FirebaseRepository repository) {
        this.repository = repository;
    }
}