package com.akshay.ostminiproject.fragments.teacher;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akshay.ostminiproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherAttendance extends Fragment {

    Context context; //always pass this context for activity intent

    public TeacherAttendance() {
        // Required empty public constructor
    }

    public static TeacherAttendance newInstance() {
        return (new TeacherAttendance());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.attendance));
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_attendance, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
