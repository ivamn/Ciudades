package com.example.ciudades.com.example.ciudades.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciudades.Operations;
import com.example.ciudades.R;
import com.example.ciudades.com.example.ciudades.pojo.LugarDestacado;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdaptadorLugaresDestacados extends FirestoreRecyclerAdapter<LugarDestacado, AdaptadorLugaresDestacados.Holder> implements View.OnClickListener, View.OnLongClickListener{

    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public AdaptadorLugaresDestacados(@NonNull FirestoreRecyclerOptions<LugarDestacado> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull LugarDestacado model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_lugares_destacados, parent, false);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        return new Holder(v);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        if (listener != null) {
            this.onClickListener = listener;
        }
    }

    public void setOnLongClickListener(View.OnLongClickListener listener) {
        if (listener != null) {
            this.onLongClickListener = listener;
        }
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onLongClickListener != null) {
            onLongClickListener.onLongClick(v);
        }
        return true;
    }

    class Holder extends RecyclerView.ViewHolder {

        private final String DEFAULT_IMAGE = "/default.jpg";

        private ImageView image;
        private TextView nombre;

        public Holder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imagenLugarDestacado);
            nombre = itemView.findViewById(R.id.nombreLugarDestacado);
        }

        public void bind(LugarDestacado lugar) {
            nombre.setText(lugar.getNombre());
            StorageReference reference = null;
            if (lugar.getImage() == null || lugar.getImage().equals("")) {
                reference = FirebaseStorage.getInstance().getReference(DEFAULT_IMAGE);
            } else {
                reference = FirebaseStorage.getInstance().getReference(lugar.getImage());
            }
            Operations.loadIntoImageView(reference, image);
        }
    }
}
