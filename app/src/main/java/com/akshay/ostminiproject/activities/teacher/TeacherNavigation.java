package com.akshay.ostminiproject.activities.teacher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.akshay.ostminiproject.R;
import com.akshay.ostminiproject.activities.login.LoginActivity;
import com.akshay.ostminiproject.fragments.AboutUs;
import com.akshay.ostminiproject.fragments.StudentProfileFragment;
import com.akshay.ostminiproject.fragments.teacher.Notification;
import com.akshay.ostminiproject.fragments.teacher.TeacherAttendance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TeacherNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.teacher_navigation_view);
        View view = navigationView.getHeaderView(0);
        TextView userName = (TextView) view.findViewById(R.id.teacher_nav_header_username);
        TextView userEnrollemntNo = (TextView) view.findViewById(R.id.teacher_nav_header_enrollment);

        auth = FirebaseAuth.getInstance();

        // this listener will be called when there is change in firebase user session
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(TeacherNavigation.this, LoginActivity.class));
                    finish();
                }
            }
        };

        auth.addAuthStateListener(authStateListener);

        userDetails = getSharedPreferences(LoginActivity.USERDETAILSSHAREDPREF, 0);
        String user = (userDetails.getString("fname", null)) + " " + (userDetails.getString("lname", null));
        String enrollment = userDetails.getString("enrollmentNo", null);

        userName.setText(user);
        userEnrollemntNo.setText(enrollment);

        // check if this activity was created from notification
        String message = getIntent().getStringExtra("message");
        if (message != null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, Notification.newInstance(message)).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teacher_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            auth.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.teacher_nav_profile) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, StudentProfileFragment.newInstance()).commit();
        } else if (id == R.id.teacher_nav_attendance) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, TeacherAttendance.newInstance()).commit();
        } else if (id == R.id.teacher_nav_abt_us) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, AboutUs.newInstance()).commit();
        } else if (id == R.id.teacher_nav_notification) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new Notification()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
