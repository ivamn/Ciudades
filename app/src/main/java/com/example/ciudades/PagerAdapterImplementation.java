package com.example.ciudades;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.firebase.firestore.DocumentReference;

public class PagerAdapterImplementation extends FragmentStatePagerAdapter {

    private int numTabs;
    private DocumentReference userReference;

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Ciudades";
            case 1:
                return "Perfil";
            default:
                return null;
        }
    }

    public PagerAdapterImplementation(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        numTabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentRecycler();
            case 1:
                return new FragmentPerfil();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
