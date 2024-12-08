package by.shug.interntrack.ui.main.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.shug.interntrack.databinding.ItemStudentsBinding;
import by.shug.interntrack.repository.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private final OnUserClickListener userClickListener;

    public UserAdapter(OnUserClickListener userClickListener) {
        this.userClickListener = userClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentsBinding binding = ItemStudentsBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemStudentsBinding binding;

        public UserViewHolder(ItemStudentsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User user) {
            binding.getRoot().setOnClickListener(v -> {
                if (userClickListener != null) {
                    userClickListener.onUserClick(user);
                }
            });
            binding.getRoot().setOnLongClickListener(view -> {
                if (userClickListener != null) {
                    userClickListener.onUserLongClick(user);
                }
                return true;
            });
            binding.tvName.setText(user.getFullName());
            binding.tvGroup.setText(user.getGroup());
            binding.tvPhone.setText(user.getPhoneNumber());
            binding.tvEmail.setText(user.getEmail());
        }
    }
}

