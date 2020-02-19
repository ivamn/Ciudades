package com.example.ciudades.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciudades.Operations;
import com.example.ciudades.R;
import com.example.ciudades.pojo.Ciudad;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Adaptador extends FirestoreRecyclerAdapter<Ciudad, Adaptador.Holder> implements View.OnClickListener, View.OnLongClickListener {

    private View.OnClickListener onClickListener;
    private View.OnLongClickListener longClickListener;

    public Adaptador(@NonNull FirestoreRecyclerOptions<Ciudad> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull Ciudad model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder, parent, false);
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(longClickListener);
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

        private TextView text;
        private ImageView imageView;

        public Holder(View v) {
            super(v);
            text = v.findViewById(R.id.text);
            imageView = v.findViewById(R.id.imageView);
        }

        public void bind(Ciudad item) {
            text.setText(String.format("%s/%s", item.getPais(), item.getCiudad()));
            StorageReference reference = FirebaseStorage.getInstance().getReference(item.getImagen());
            Operations.loadIntoImageView(reference, imageView);
        }
    }

}
