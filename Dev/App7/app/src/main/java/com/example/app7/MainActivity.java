package com.example.app7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        List<String> titles = Arrays.asList("1", "2", "3", "4");
        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(titles);

        viewPager.setAdapter(pagerAdapter);

        //viewPager.setUserInputEnabled(false);
        //viewPager.set
        viewPager.setCurrentItem(2, false);

    }
}