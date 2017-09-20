package com.akshay.ostminiproject.activities.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akshay.ostminiproject.R;
import com.akshay.ostminiproject.activities.notificationmsg.app.Config;
import com.akshay.ostminiproject.activities.student.MainNavigation;
import com.akshay.ostminiproject.activities.teacher.TeacherNavigation;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;

    private final String[] fname = new String[1];
    private final String[] lname = new String[1];
    private final String[] enrollmentNo = new String[1];
    private final String[] userType = new String[1];
    private final String[] fireBaseUid = new String[1];

    private String uid;

    public static final String USERDETAILSSHAREDPREF = "userDetailsSharedPref";
    SharedPreferences userDetails;

    public static final String TAG = "UserReqTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(this);

        if(auth.getCurrentUser()!= null) {
            goToActivity(getSharedPreferences(USERDETAILSSHAREDPREF, 0).getString("userType", ""));
            finish();
        }

        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button resetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError(getString(R.string.enter_email));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError(getString(R.string.enter_password));
                    return;
                }

                if (password.length() < 6) {
                    inputPassword.setError(getString(R.string.minimum_password));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /* If sign in fails, display a message to the user. If sign in succeeds
                           the auth state listener will be notified and logic to handle the
                           signed in user can be handled in the listener.*/

                        if(!task.isSuccessful()){
                            // error in login
                            Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            if (auth.getCurrentUser() != null) {
                                uid = auth.getCurrentUser().getUid();
                            }

                            StringRequest stringRequest = makeRequest();
                            stringRequest.setTag(TAG);

                            requestQueue.add(stringRequest);
                        }
                    }
                });
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    StringRequest makeRequest() {

        String SEND_URL = "http://ostminiproject.000webhostapp.com/getUserType.php";
        return new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonRootObject = new JSONObject(response);
                    JSONArray jsonArray = jsonRootObject.optJSONArray("data");
                    JSONObject jsonObject= jsonArray.getJSONObject(0);
                    fireBaseUid[0] =jsonObject.optString("firebaseuid");
                    enrollmentNo[0] =jsonObject.optString("enrollment_number");
                    fname[0] =jsonObject.optString("fname");
                    lname[0] =jsonObject.optString("lname");
                    userType[0] =jsonObject.optString("user_type");

                    Toast.makeText(LoginActivity.this,fname[0]+"\nUser Type: "+userType[0], Toast.LENGTH_LONG).show();

                    if (saveUserDetailsSharedPref()) {
                        goToActivity(userType[0]);
                    }
                    progressBar.setVisibility(View.GONE);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error","error fetching details via volley");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",uid);
                return params;
            }
        };
    }

    boolean saveUserDetailsSharedPref() {
        userDetails = getSharedPreferences(USERDETAILSSHAREDPREF, 0);
        SharedPreferences.Editor editor = userDetails.edit();
        editor.putString("firebaseUid", fireBaseUid[0]);
        editor.putString("enrollmentNo", enrollmentNo[0]);
        editor.putString("userType", userType[0]);
        editor.putString("fname", fname[0]);
        editor.putString("lname", lname[0]);
        return editor.commit();
    }

    void goToActivity(String userType) {
        if (userType.equals("1")) {
            startActivity(new Intent(LoginActivity.this, MainNavigation.class));
            Config.TOPIC_GLOBAL = "student";
            FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
        } else if (userType.equals("0")) {
            startActivity(new Intent(LoginActivity.this, TeacherNavigation.class));
            Config.TOPIC_GLOBAL = "teacher";
            FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
        }
    }
}
