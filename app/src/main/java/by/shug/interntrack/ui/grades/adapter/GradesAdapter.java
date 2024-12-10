package by.shug.interntrack.ui.grades.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import by.shug.interntrack.databinding.ItemGradesBinding;
import by.shug.interntrack.repository.model.Grade;

public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.GradesViewHolder> {

    private final List<Grade> grades = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setGrades(List<Grade> newGrades) {
        grades.clear();
        grades.addAll(newGrades);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GradesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGradesBinding binding = ItemGradesBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new GradesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GradesViewHolder holder, int position) {
        Grade grade = grades.get(position);
        holder.bind(grade);
    }

    @Override
    public int getItemCount() {
        return grades.size();
    }

    static class GradesViewHolder extends RecyclerView.ViewHolder {
        private final ItemGradesBinding binding;

        public GradesViewHolder(@NonNull ItemGradesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Grade grade) {
            binding.tvCompanyName.setText(grade.getCompanyName());
            binding.tvPosition.setText(grade.getPosition());
            binding.tvGrade.setText(grade.getGradeValue());
        }
    }
}
