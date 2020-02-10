package com.example.ciudades;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentEditLugar extends Fragment {

    private static final int COD_ELEGIR_IMAGEN = 0;
    private EditText editLugar, editDescripcion;
    private ImageView imageView;
    private Util.Accion accion;
    private Lugar lugar;
    private String key;
    private Uri selectedImage;
    private AlertDialog dialog;

    public FragmentEditLugar(LugarContainer lugarContainer) {
        accion = lugarContainer.getAccion();
        lugar = lugarContainer.getLugar();
        key = lugarContainer.getKey();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.edit_lugar_fragment, container, false);
        showProgressBar();
        editLugar = v.findViewById(R.id.editLugar);
        editDescripcion = v.findViewById(R.id.editDescripcion);
        imageView = v.findViewById(R.id.imageViewLugar);
        if (accion == Util.Accion.EDIT_REQUEST) {
            // Progress bar
            FirebaseStorage.getInstance().getReference(key).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    selectedImage = task.getResult();
                    dialog.cancel();
                }
            });
            editLugar.setText(lugar.getLugar());
            editDescripcion.setText(lugar.getDescripcion());
            if (!lugar.getImagen().equals("") && lugar.getImagen() != null) {
                Operations.loadIntoImageView(FirebaseStorage.getInstance().getReference(lugar.getImagen()), imageView);
            }
        } else {
            key = Operations.newId();
        }
        Operations.placeDocument = Operations.placeCollection.document(key);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, COD_ELEGIR_IMAGEN);
                }
            }
        });

        LugarViewModel lugarViewModel = new ViewModelProvider(getActivity()).get(LugarViewModel.class);
        v.findViewById(R.id.buttonAceptarLugar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Lugar lugar = generarLugar();
                if (accion == Util.Accion.ADD_REQUEST) {
                    Operations.addPlace(lugar, selectedImage);
                    getParentFragmentManager().popBackStack();
                } else {
                    Operations.updatePlace(lugar, key, selectedImage);
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        return v;
    }

    private void showProgressBar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(getLayoutInflater().inflate(R.layout.progress, null));
        builder.setCancelable(false);
        dialog = builder.create();

    }

    private Lugar generarLugar() {
        return new Lugar(editLugar.getEditableText().toString(), editDescripcion.getText().toString(), key);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Se ha cancelado la operación", Toast.LENGTH_SHORT).show();
        }
    }
}
