package by.shug.interntrack.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import by.shug.interntrack.databinding.FragmentMainBinding;
import by.shug.interntrack.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends BaseFragment<FragmentMainBinding, MainViewModel> {

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

    }
}