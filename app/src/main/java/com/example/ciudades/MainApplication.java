package com.example.ciudades;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainApplication extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_main);
        mostrarRecycler();
    }

    private void mostrarRecycler(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FragmentRecycler fragmentRecycler = new FragmentRecycler();
        ft.add(R.id.fragment_container, fragmentRecycler);
        ft.addToBackStack(null);
        ft.commit();
    }
}
