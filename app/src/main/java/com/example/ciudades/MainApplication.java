package com.example.ciudades;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainApplication extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_main);
        mostrarFragmentTabs();
    }

    private void mostrarFragmentTabs(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FragmentTabs fragmentTabs = new FragmentTabs();
        ft.add(R.id.fragment_container, fragmentTabs);
        ft.addToBackStack(null);
        ft.commit();
    }
}
