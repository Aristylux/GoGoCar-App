package com.example.app7;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHomes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHomes extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FG_NAME = "name";

    private String fgName;
    int ctn = 0;
    boolean isEn = false;

    public FragmentHomes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentName Parameter 2.
     * @return A new instance of fragment FragmentHomes.
     */
    public static FragmentHomes newInstance(String fragmentName) {
        FragmentHomes fragment = new FragmentHomes();
        Bundle args = new Bundle();
        args.putString(ARG_FG_NAME, fragmentName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fgName = getArguments().getString(ARG_FG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homes, container, false);

        TextView text = view.findViewById(R.id.fg_name);
        text.setText(fgName);

        TextView counter = view.findViewById(R.id.counter);
        counter.setText(String.valueOf(ctn));

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctn++;
                counter.setText(String.valueOf(ctn));
            }
        });

        Button buttonBlock = view.findViewById(R.id.button_block);
        buttonBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEn = !isEn;
                //.setEnabled(isEn);
            }
        });

        return view;
    }
}