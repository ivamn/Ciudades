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

public class Adaptador extends FirestoreRecyclerAdapter<Ciudad, Adaptador.Holder> {

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
        return new Holder(view);
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
            text.setText(String.format("%s/%s", item.getPais(), item.getNombre()));
            if (item.getImagen() == null || item.getImagen().equals("")) {
                StorageReference reference = FirebaseStorage.getInstance().getReference(DEFAULT_IMAGE);
                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Picasso.get().load(task.getResult()).into(imageView);
                    }
                });
            } else {
                StorageReference reference = FirebaseStorage.getInstance().getReference(item.getImagen());
                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Picasso.get().load(task.getResult()).into(imageView);
                    }
                });
            }
        }
    }

}
