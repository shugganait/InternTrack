package by.shug.interntrack.ui.grades;


import static by.shug.interntrack.base.Constants.COMPANYNAME;
import static by.shug.interntrack.base.Constants.GRADE;
import static by.shug.interntrack.base.Constants.GRADES;
import static by.shug.interntrack.base.Constants.POSITION;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentGradesBinding;
import by.shug.interntrack.repository.model.Grade;
import by.shug.interntrack.ui.grades.adapter.GradesAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GradesFragment extends BaseFragment<FragmentGradesBinding, GradesViewModel> {

    private GradesAdapter gradesAdapter;

    @Override
    protected FragmentGradesBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentGradesBinding.inflate(getLayoutInflater());
    }

    @Override
    protected Class<GradesViewModel> getViewModelClass() {
        return GradesViewModel.class;
    }

    @Override
    protected void uiBox() {
        initAdapter();
        getGrades();
    }

    private void getGrades() {
        viewModel.getUserGrades(viewModel.getCurrentUser().getUid(), task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List<Map<String, Object>> userGrades = (List<Map<String, Object>>) document.get(GRADES);

                List<Grade> gradeList = new ArrayList<>();
                for (Map<String, Object> gradeMap : userGrades) {
                    String companyName = (String) gradeMap.get(COMPANYNAME);
                    String position = (String) gradeMap.get(POSITION);
                    String gradeValue = (String) gradeMap.get(GRADE);

                    gradeList.add(new Grade(companyName, position, gradeValue));
                }
                gradesAdapter.setGrades(gradeList);
            }
        });
    }

    private void initAdapter() {
        getBinding.rvGrades.setLayoutManager(new LinearLayoutManager(requireContext()));

        gradesAdapter = new GradesAdapter();
        getBinding.rvGrades.setAdapter(gradesAdapter);
    }

}