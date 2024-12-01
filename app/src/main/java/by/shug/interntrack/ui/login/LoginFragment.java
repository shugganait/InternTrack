package by.shug.interntrack.ui.login;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import by.shug.interntrack.base.BaseFragment;
import by.shug.interntrack.databinding.FragmentLoginBinding;


public class LoginFragment extends BaseFragment<FragmentLoginBinding> {

    @Override
    protected FragmentLoginBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void uiBox() {

    }
}