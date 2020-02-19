package com.example.ciudades.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.ciudades.Operations;
import com.example.ciudades.PagerAdapterImplementation;
import com.example.ciudades.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;

public class FragmentTabs extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.tabs_fragment, container, false);
        // Creación e inicialización de las tabs
        TabLayout tabs = v.findViewById(R.id.tab_layout);
        final ViewPager pager = v.findViewById(R.id.view_pager);
        PagerAdapterImplementation adapter = new PagerAdapterImplementation(getActivity().getSupportFragmentManager(), 2);
        tabs.setupWithViewPager(pager);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // Si es la primera vez del usuario, muestra la pantalla del perfil
        Operations.userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Boolean b = task.getResult().getBoolean("primera_vez");
                if (b == null) {
                    pager.setCurrentItem(1);
                }
            }
        });
        return v;
    }
}
