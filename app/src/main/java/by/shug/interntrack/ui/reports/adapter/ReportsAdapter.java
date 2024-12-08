package by.shug.interntrack.ui.reports.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.shug.interntrack.databinding.ItemReportsBinding;
import by.shug.interntrack.repository.model.Report;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {

    private List<Report> reportList;

    public ReportsAdapter() {}

    public void setReportList(List<Report> reports) {
        this.reportList = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReportsBinding binding = ItemReportsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ReportViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.bind(report);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        private final ItemReportsBinding binding;

        public ReportViewHolder(@NonNull ItemReportsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Report report) {
//            binding.tvDate.setText(report.getDate());
//            binding.tvContent.setText(report.getContent());
        }
    }
}
