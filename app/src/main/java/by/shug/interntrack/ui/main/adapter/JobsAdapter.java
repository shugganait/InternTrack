package by.shug.interntrack.ui.main.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.shug.interntrack.databinding.ItemJobsBinding;
import by.shug.interntrack.repository.model.Job;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobsViewHolder> {

    private List<Job> jobsList;

    public JobsAdapter(List<Job> jobsList) {
        this.jobsList = jobsList;
    }

    public void setJobsList(List<Job> jobsList) {
        this.jobsList = jobsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJobsBinding binding = ItemJobsBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new JobsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JobsViewHolder holder, int position) {
        Job jobs = jobsList.get(position);
        holder.bind(jobs);
    }

    @Override
    public int getItemCount() {
        return jobsList != null ? jobsList.size() : 0;
    }

    static class JobsViewHolder extends RecyclerView.ViewHolder {

        private final ItemJobsBinding binding;

        public JobsViewHolder(ItemJobsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Job jobs) {
            binding.tvName.setText(jobs.getCompanyName());
            binding.tvPosition.setText(jobs.getPosition());
            binding.tvPhone.setText(jobs.getPhoneNumber());
            Log.d("shug", "bind: " + jobs.getPhoneNumber());
            binding.tvAddress.setText(jobs.getAddress());
        }
    }
}
