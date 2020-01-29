package com.example.ciudades;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MainApplication extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_main);

        mostrarFragmentTabs();

        CiudadViewModel ciudadViewModel = new ViewModelProvider(this).get(CiudadViewModel.class);
        ciudadViewModel.getData().observe(this, new Observer<CiudadContainer>() {
            @Override
            public void onChanged(CiudadContainer ciudadContainer) {
                switch (ciudadContainer.getAccion()) {
                    case ADD_REQUEST:
                        replaceFragmentEdit();
                        break;
                    case ADD_ACTION:
                        addCiudadFirebase();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void replaceFragmentEdit() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentEdit fragment = new FragmentEdit();
        ft.add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void mostrarFragmentTabs(){
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentTabs fragment = new FragmentTabs();
        ft.replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void addCiudadFirebase() {
        eliminarUltimoFragment();
        // Firebase añadir ciudad
    }

    private void eliminarUltimoFragment(){
        getSupportFragmentManager().popBackStack();
    }
}
