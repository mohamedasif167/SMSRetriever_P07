package com.example.a15017498.smsretrieverps;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        fragmentNum f1 = new fragmentNum();
        ft.replace(R.id.frame1,f1);

        fragmentWord f2 = new fragmentWord();
        ft.replace(R.id.frame2,f2);


        ft.commit();


    }
}
