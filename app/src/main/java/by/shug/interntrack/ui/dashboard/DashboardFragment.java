package by.shug.interntrack.ui.dashboard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentDashboardBinding;

public class DashboardFragment extends BaseFragment<FragmentDashboardBinding> {

    private FragmentDashboardBinding binding;

    @Override
    protected FragmentDashboardBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDashboardBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void uiBox() {
        binding = getBinding();
        binding.tvText.setText("HELOOOOOOOOOOOOOOOOOOOOOO");
    }
}