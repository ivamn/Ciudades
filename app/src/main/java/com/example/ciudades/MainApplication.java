package com.example.ciudades;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ciudades.fragments.FragmentTabs;

public class MainApplication extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_main);
        Operations.applicationContext = this;
        mostrarFragmentTabs();
        mostrarDialogoInformativo();
    }

    private void mostrarFragmentTabs() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentTabs fragment = new FragmentTabs();
        ft.replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void mostrarDialogoInformativo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información");
        builder.setMessage("Si no se cargan las imágenes tras cambiarlas, actualizar con el botón de refrescar junto al de añadir");
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
