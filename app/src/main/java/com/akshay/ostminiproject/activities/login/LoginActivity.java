package com.akshay.ostminiproject.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akshay.ostminiproject.R;
import com.akshay.ostminiproject.activities.notificationmsg.app.Config;
import com.akshay.ostminiproject.activities.student.MainNavigation;
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

import static com.android.volley.Request.Method.POST;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnLogin, resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String SEND_URL="http://ostminiproject.000webhostapp.com/getUserType.php";

        auth = FirebaseAuth.getInstance();
        final String[] fname = new String[1];
        final String[] lname = new String[1];
        final String[] enrollmentNo = new String[1];
        final String[] userType = new String[1];
        final String[] fireBaseUid = new String[1];

        if(auth.getCurrentUser()!= null) {
            // TODO: get the user type based on uid and open the activity accordingly. Remember this is to be done at 2 places
            final String uid = auth.getCurrentUser().getUid();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Toast.makeText(MobileDetails.this, response, Toast.LENGTH_LONG).show();

                    try
                    {
                        JSONObject jsonRootObject = new JSONObject(response);
                        JSONArray jsonArray = jsonRootObject.optJSONArray("data");
                        JSONObject jsonObject= jsonArray.getJSONObject(0);
                        fireBaseUid[0] =jsonObject.optString("firebaseuid").toString();
                        enrollmentNo[0] =jsonObject.optString("enrollment_number").toString();
                        fname[0] =jsonObject.optString("fname").toString();
                        lname[0] =jsonObject.optString("lname").toString();
                        userType[0] =jsonObject.optString("user_type").toString();

                        Toast.makeText(LoginActivity.this,fname[0]+"\nUser Type: "+userType[0], Toast.LENGTH_LONG).show();




                    }
                    catch (Exception e)
                    {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("username",uid);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


            // if student TODO: make necessary changes based on type of user
            startActivity(new Intent(LoginActivity.this, MainNavigation.class));
            FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
            // if teacher
            //startActivity(new Intent(LoginActivity.this, TeacherNavigation.class)); // remember to import class
            finish();
        }

        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.btn_login);
        resetPassword = (Button) findViewById(R.id.btn_reset_password);

        auth = FirebaseAuth.getInstance();

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
                        } else {
                            // TODO: get the user type based on uid and open the activity accordingly
                            String uid;
                            if (auth.getCurrentUser() != null) {
                                uid = auth.getCurrentUser().getUid();
                            }

                            // TODO: make necessary changes based on type of user
                            // if student
                            Intent intent = new Intent(LoginActivity.this, MainNavigation.class);
                            // if teacher
                            // Intent intent = new Intent(LoginActivity.this, TeacherNavigation.class); // remember to import class
                            FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);
                            finish();
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
}
