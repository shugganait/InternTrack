package by.shug.interntrack.ui.dashboard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import by.shug.interntrack.databinding.FragmentDashboardBinding;
import by.shug.interntrack.base.BaseFragment;

public class DashboardFragment extends BaseFragment<FragmentDashboardBinding> {

    @Override
    protected FragmentDashboardBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDashboardBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void uiBox() {

    }
}