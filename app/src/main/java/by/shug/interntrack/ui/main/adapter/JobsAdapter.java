package by.shug.interntrack.ui.main.adapter;

import android.annotation.SuppressLint;
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
    private final OnJobClickListener jobClickListener;

    public JobsAdapter(OnJobClickListener jobClickListener) {
        this.jobClickListener = jobClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
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

    public class JobsViewHolder extends RecyclerView.ViewHolder {

        private final ItemJobsBinding binding;

        public JobsViewHolder(ItemJobsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Job job) {
            binding.getRoot().setOnClickListener(v -> {
                if (jobClickListener != null) {
                    jobClickListener.onJobClick(job);
                }
            });
            binding.tvName.setText(job.getCompanyName());
            binding.tvPosition.setText(job.getPosition());
            binding.tvPhone.setText(job.getPhoneNumber());
            Log.d("shug", "bind: " + job.getPhoneNumber());
            binding.tvAddress.setText(job.getAddress());
        }
    }
}
