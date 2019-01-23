package com.example.gougxl.mapviewfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);



        FragmentManager fm=getSupportFragmentManager();
        Fragment fragmentleft = fm.findFragmentById(R.id.fragment_container_left);
        Fragment fragmentright= fm.findFragmentById(R.id.fragment_container_right);

        if (fragmentleft==null){
            fragmentleft=new MapViewFragment();
            fm.beginTransaction().add(R.id.fragment_container_left,fragmentleft).commit();
        }
        if (fragmentright==null){
            fragmentright=new MapViewFragment();
            fm.beginTransaction().add(R.id.fragment_container_right,fragmentright).commit();
        }
    }
}
