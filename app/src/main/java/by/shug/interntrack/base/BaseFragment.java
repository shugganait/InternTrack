package by.shug.interntrack.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment<VB extends ViewBinding, VM extends ViewModel> extends Fragment {

    private VB binding;
    protected VM viewModel;

    protected VB binding() {
        if (binding == null) {
            throw new IllegalStateException("Binding is not initialized");
        }
        return binding;
    }

    protected abstract VB inflateBinding(LayoutInflater inflater, ViewGroup container);

    protected abstract Class<VM> getViewModelClass();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = inflateBinding(inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(getViewModelClass()); // Инициализация ViewModel
        uiBox();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Метод для пользовательской логики в onViewCreated
    protected abstract void uiBox();
}
