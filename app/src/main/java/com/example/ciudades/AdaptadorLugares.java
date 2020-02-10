package com.example.ciudades;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AdaptadorLugares extends FirestoreRecyclerAdapter<Lugar, AdaptadorLugares.Holder> implements View.OnClickListener, View.OnLongClickListener {

    private View.OnClickListener onClickListener;
    private View.OnLongClickListener longClickListener;

    public AdaptadorLugares(@NonNull FirestoreRecyclerOptions<Lugar> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull Lugar model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_lugares, parent, false);
        view.setOnClickListener(onClickListener);
        return new Holder(view);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            this.onClickListener = onClickListener;
        }
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    public void setOnLongClickListener(View.OnLongClickListener longClickListener) {
        if (longClickListener != null) {
            this.longClickListener = longClickListener;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (longClickListener != null) {
            longClickListener.onLongClick(v);
        }
        return true;
    }

    class Holder extends RecyclerView.ViewHolder {
        private final String DEFAULT_IMAGE = "/default.jpg";

        private TextView textLugar;
        private TextView textDescripcion;
        private ImageView imageView;

        public Holder(View v) {
            super(v);
            textLugar = v.findViewById(R.id.textLugar);
            textDescripcion = v.findViewById(R.id.textDescripcion);
            imageView = v.findViewById(R.id.imagenLugar);
        }

        public void bind(Lugar item) {
            textLugar.setText(item.getLugar());
            textDescripcion.setText(item.getDescripcion());
            StorageReference reference = null;
            if (item.getImagen() == null || item.getImagen().equals("")) {
                reference = FirebaseStorage.getInstance().getReference(DEFAULT_IMAGE);
            } else {
                reference = FirebaseStorage.getInstance().getReference(item.getImagen());
            }
            Operations.loadIntoImageView(reference, imageView);
        }
    }

}
