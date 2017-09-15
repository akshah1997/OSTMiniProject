package com.akshay.ostminiproject.fragments.teacher;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akshay.ostminiproject.R;
import com.akshay.ostminiproject.activities.notificationmsg.app.Config;
import com.akshay.ostminiproject.activities.notificationmsg.util.NotificationUtils;

public class Notification extends Fragment {

    private String message;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private TextView textView;

    public Notification() {
        // Required empty public constructor
    }

    public static Notification newInstance(String message) {
        Notification fragment = new Notification();
        Bundle args = new Bundle();
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString("message");
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context onreceivecontext, Intent intent) {
                if(intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    Toast.makeText(context, "Push Notification: " + message, Toast.LENGTH_SHORT).show();
                    textView.setText(message);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Notification");
        View view = inflater.inflate(R.layout.fragment_notification_teacher, container, false);
        textView = (TextView) view.findViewById(R.id.teacher_notif_message);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getActivity().getApplicationContext());
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
