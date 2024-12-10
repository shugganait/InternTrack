package by.shug.interntrack.ui.reports;

import static by.shug.interntrack.base.Constants.COMPANYNAME;
import static by.shug.interntrack.base.Constants.DATE;
import static by.shug.interntrack.base.Constants.FULLNAME;
import static by.shug.interntrack.base.Constants.JOB;
import static by.shug.interntrack.base.Constants.POSITION;
import static by.shug.interntrack.base.Constants.REPORTCONTENT;
import static by.shug.interntrack.base.Constants.REPORTS;
import static by.shug.interntrack.ui.main.MainFragment.MAIN_UID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentReportsBinding;
import by.shug.interntrack.repository.model.Report;
import by.shug.interntrack.ui.reports.adapter.ReportsAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReportsFragment extends BaseFragment<FragmentReportsBinding, ReportsViewModel> {

    private NavController navController;
    private ReportsAdapter reportsAdapter;
    private String userId;

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
        Bundle bundle = getArguments();
        userId = bundle != null ? bundle.getString(MAIN_UID) : null;
        navController = Navigation.findNavController(getBinding.getRoot());
        initAdapter();
        initListener();
        getReports();
    }

    private void initListener() {
        getBinding.btnSetGrade.setOnClickListener(v -> {
            if (!getBinding.etGrade.getText().toString().isEmpty()) {
                setGrade();
            }
        });
    }

    private void setGrade() {
        getBinding.loadingContainer.setVisibility(View.VISIBLE);
        String name = getBinding.tvFullname.getText().toString();
        String companyName = getBinding.tvCompanyName.getText().toString();
        String position = getBinding.tvPosition.getText().toString();
        String grade = getBinding.etGrade.getText().toString();
        viewModel.addUserGrade(userId, name, companyName, position, grade, task -> {
            if (task.isSuccessful()) {
                showToast("Оценка поставлена");
                getBinding.loadingContainer.setVisibility(View.GONE);
                navController.navigateUp();
            } else {
                showToast("Ошибка");
                getBinding.loadingContainer.setVisibility(View.GONE);
            }
        });
    }

    private void getReports() {
        viewModel.getUserReports(userId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot snapshot = task.getResult();
                List<Map<String, Object>> reports = (List<Map<String, Object>>) snapshot.get(REPORTS);

                if (reports != null) {

                    String fullName = snapshot.getString(FULLNAME);

                    // Извлекаем job (companyName и position)
                    Map<String, String> jobData = (Map<String, String>) snapshot.get(JOB);
                    String companyName = jobData != null ? jobData.get(COMPANYNAME) : "N/A";
                    String position = jobData != null ? jobData.get(POSITION) : "N/A";

                    getBinding.tvFullname.setText(fullName);
                    getBinding.tvCompanyName.setText(companyName);
                    getBinding.tvPosition.setText(position);

                    List<Report> reportList = new ArrayList<>();
                    for (Map<String, Object> reportData : reports) {
                        String date = (String) reportData.get(DATE);
                        String content = (String) reportData.get(REPORTCONTENT);

                        reportList.add(new Report(date, content));
                    }

                    reportsAdapter.setReports(reportList);
                } else {
                    showToast("Отчеты не найдены");
                }
            } else {
                showToast("Ошибка");
            }
        });
    }

    private void initAdapter() {
        getBinding.rvReports.setLayoutManager(new LinearLayoutManager(requireContext()));

        reportsAdapter = new ReportsAdapter();
        getBinding.rvReports.setAdapter(reportsAdapter);
    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}