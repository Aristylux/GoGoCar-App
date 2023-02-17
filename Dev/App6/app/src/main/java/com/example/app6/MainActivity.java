package com.example.app6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

public class MainActivity extends AppCompatActivity {

    private SlidrInterface slidr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .scrimColor(Color.TRANSPARENT)
                .scrimStartAlpha(0f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(false)
                .build();

        // Create a new Slidr instance with customization options
        slidr = Slidr.attach(this, config);

        MainFragment fragment = MainFragment.newInstance("Main Fragment");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(slidr == null)
            slidr = Slidr.replace(this.findViewById(R.id.main_container), new SlidrConfig.Builder().position(SlidrPosition.LEFT).build());
    }

}