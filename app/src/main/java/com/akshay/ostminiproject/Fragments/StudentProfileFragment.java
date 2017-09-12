package com.akshay.ostminiproject.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akshay.ostminiproject.Activities.Login.LoginActivity;
import com.akshay.ostminiproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentProfileFragment extends Fragment implements View.OnClickListener{

    private Button changePassword, signOut, confirmPassword;
    private EditText newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    Context context;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    public static StudentProfileFragment newInstance() {
        return (new StudentProfileFragment());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        // this listener will be called when there is change in firebase user session
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            }
        };

        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Profile");
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);

        changePassword = (Button) view.findViewById(R.id.profile_btn_change_password);
        changePassword.setOnClickListener(this);

        confirmPassword = (Button) view.findViewById(R.id.profile_btn_confirm_pwd);
        confirmPassword.setOnClickListener(this);

        signOut = (Button) view.findViewById(R.id.profile_btn_signout);
        signOut.setOnClickListener(this);

        progressBar = (ProgressBar) view.findViewById(R.id.profile_progressbar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        newPassword = (EditText) view.findViewById(R.id.profile_newPassword);

        newPassword.setVisibility(View.GONE);
        confirmPassword.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.profile_btn_change_password) {
            changePassword.setVisibility(View.GONE);
            signOut.setVisibility(View.GONE);
            newPassword.setVisibility(View.VISIBLE);
            confirmPassword.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.profile_btn_confirm_pwd) {
            progressBar.setVisibility(View.VISIBLE);
            if (FirebaseAuth.getInstance().getCurrentUser() != null && !newPassword.getText().toString().equals("")) {
                if (newPassword.getText().toString().length() < 6) {
                    newPassword.setError(getString(R.string.minimum_password));
                } else {
                    try {
                        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Password is updated! Sign in with new password!", Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(getActivity(), "Failed to update password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    catch(NullPointerException e) {
                        Toast.makeText(getActivity(), "Unexpected error in changing the password!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (newPassword.getText().toString().trim().equals("")) {
                newPassword.setError(getString(R.string.enter_password));
                progressBar.setVisibility(View.GONE);
            }
        } else {
            //onclick signout
            auth.signOut();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
