package com.example.ciudades;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ciudades.com.example.ciudades.fragments.FragmentTabs;

public class MainApplication extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_main);
        Operations.applicationContext = this;
        mostrarFragmentTabs();
    }

    private void mostrarFragmentTabs() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentTabs fragment = new FragmentTabs();
        ft.replace(R.id.fragment_container, fragment)
                .commit();
    }
}
