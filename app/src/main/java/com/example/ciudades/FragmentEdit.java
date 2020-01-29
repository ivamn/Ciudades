package com.example.ciudades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FragmentEdit extends Fragment {

    private EditText ciudad, pais;
    private ImageView imageView;
    private CiudadViewModel ciudadViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.edit_fragment, container, false);
        ciudad = v.findViewById(R.id.editCiudad);
        pais = v.findViewById(R.id.editPais);
        imageView = v.findViewById(R.id.imageView);
        ciudadViewModel = new ViewModelProvider(getActivity()).get(CiudadViewModel.class);
        v.findViewById(R.id.buttonAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ciudadViewModel.setData(new CiudadContainer(null, CiudadViewModel.Accion.ADD_ACTION));
            }
        });
        return v;
    }
}
