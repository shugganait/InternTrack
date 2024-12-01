package by.shug.interntrack.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import by.shug.interntrack.databinding.FragmentMainBinding;
import by.shug.interntrack.base.BaseFragment;

public class MainFragment extends BaseFragment<FragmentMainBinding> {

    private FragmentMainBinding binding;

    @Override
    protected FragmentMainBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void uiBox() {
        binding = getBinding();
    }
}