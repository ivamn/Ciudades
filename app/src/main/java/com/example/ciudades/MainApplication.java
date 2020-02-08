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
        Operations.applicationContext = this;
        mostrarFragmentTabs();

        LugarViewModel lugarViewModel = new ViewModelProvider(this).get(LugarViewModel.class);
        lugarViewModel.getData().observe(this, new Observer<LugarContainer>() {
            @Override
            public void onChanged(LugarContainer lugarContainer) {
                switch (lugarContainer.getAccion()) {
                    case ADD_REQUEST:
                    case EDIT_REQUEST:
                        replaceFragmentEditLugar(lugarContainer);
                        break;
                    default:
                        break;
                }
            }
        });
        CiudadViewModel ciudadViewModel = new ViewModelProvider(this).get(CiudadViewModel.class);
        ciudadViewModel.getData().observe(this, new Observer<CiudadContainer>() {
            @Override
            public void onChanged(CiudadContainer ciudadContainer) {
                switch (ciudadContainer.getAccion()) {
                    case ADD_REQUEST:
                    case EDIT_REQUEST:
                        replaceFragmentEdit(ciudadContainer);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void replaceFragmentEdit(CiudadContainer ciudadContainer) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentEdit fragment = new FragmentEdit(ciudadContainer);
        ft.add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void replaceFragmentEditLugar(LugarContainer lugarContainer) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentEditLugar fragment = new FragmentEditLugar(lugarContainer);
        ft.add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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
