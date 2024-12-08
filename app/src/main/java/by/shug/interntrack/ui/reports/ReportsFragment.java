package by.shug.interntrack.ui.reports;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentReportsBinding;
import by.shug.interntrack.ui.reports.adapter.ReportsAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReportsFragment extends BaseFragment<FragmentReportsBinding, ReportsViewModel> {

    private NavController navController;
    private ReportsAdapter reportsAdapter;

    @Override
    protected FragmentReportsBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentReportsBinding.inflate(getLayoutInflater());
    }

    @Override
    protected Class<ReportsViewModel> getViewModelClass() {
        return ReportsViewModel.class;
    }

    @Override
    protected void uiBox() {
        navController = Navigation.findNavController(getBinding.getRoot());
        initAdapter();
    }

    private void initAdapter() {
//        getBinding.rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));

        reportsAdapter = new ReportsAdapter();
//        getBinding.rvStudents.setAdapter(userAdapter);
    }
}