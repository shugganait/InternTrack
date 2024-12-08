package by.shug.interntrack.ui.main.adapter;

import by.shug.interntrack.repository.model.User;

public interface OnUserClickListener {
    void onUserClick(User data);
    void onUserLongClick(User data);
}
