package com.ptglove.frag;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ptglove.R;

public class LoginFragment extends Fragment {

    private FragmentManager fragmentManager;
    private SharedPreferences preferences;
    private EditText text;
    private String password;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getParentFragmentManager();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        password = "";
        if (preferences.contains("master_password")) {
            password = preferences.getString("master_password", "");
        }
        if (password.isEmpty()) {
            fragmentManager.beginTransaction().replace(R.id.flContent, new SettingsFragment()).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        text = (EditText) getView().findViewById(R.id.login_password);
        getView().findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().equals(password)) {
                    fragmentManager.beginTransaction().replace(R.id.flContent, new SettingsFragment()).commit();
                } else {
                    text.getText().clear();
                    Toast.makeText(getContext(), "Password incorrect, try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}