package com.example.ciudades;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class Operations {

    public static final String DEFAULT_URL = "/default.jpg";
    public static final StorageReference DEFAULT_IMAGE = FirebaseStorage.getInstance().getReference(DEFAULT_URL);

    public static FirebaseUser user;
    public static DocumentReference userDocument;
    public static CollectionReference cityCollection;
    public static DocumentReference cityDocument;
    public static CollectionReference placeCollection;
    public static DocumentReference placeDocument;
    public static CollectionReference photoCollection;
    public static Context applicationContext;

    public static void initializeReferences() {
        FirebaseFirestore fire = FirebaseFirestore.getInstance();
        if (user != null) {
            userDocument = fire.collection("usuarios").document(user.getEmail());
            cityCollection = userDocument.collection("ciudades");
        }
    }

    public static void deleteCity(final String key) {
        cityCollection.document(key).collection("lugares").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot d : task.getResult().getDocuments()) {
                    cityCollection.document(key).collection("lugares").document(d.getId()).delete();
                }
            }
        });
        cityCollection.document(key).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(applicationContext, "No se ha podido borrar la ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deletePlace(String key) {
        placeCollection.document(key).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(applicationContext, "No se ha podido borrar la ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void addCity(final Ciudad c, final Uri image) {
        final String randomString = getRandomString();
        if (image == null) {
            c.setImagen(DEFAULT_URL);
            cityCollection.document(randomString).set(c);
        } else {
            c.setImagen(randomString);
            uploadImage(randomString, image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    cityCollection.document(randomString).set(c);
                }
            });
        }
    }

    public static void addPlace(final Lugar lugar, final Uri image) {
        final String randomString = getRandomString();
        if (image == null) {
            lugar.setImagen(DEFAULT_URL);
            placeCollection.document(randomString).set(lugar);
        } else {
            lugar.setImagen(randomString);
            uploadImage(randomString, image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    placeCollection.document(randomString).set(lugar);
                }
            });
        }
    }

    private static String getRandomString() {
        StringBuilder builder = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 15; i++) {
            builder.append(r.nextInt(50));
        }
        return builder.toString();
    }

    private static void updateCity(Ciudad newCity, String documentKey) {
        cityCollection.document(documentKey).set(newCity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(applicationContext, "No se ha podido actualizar la ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void updatePlace(Lugar newLugar, String documentKey) {
        placeCollection.document(documentKey).set(newLugar).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(applicationContext, "No se ha podido actualizar la ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void updateCity(final Ciudad newCity, final String key, Uri image) {
        if (image == null) {
            newCity.setImagen(DEFAULT_URL);
            updateCity(newCity, key);
        } else {
            uploadImage(newCity.getImagen(), image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        updateCity(newCity, key);
                    }
                }
            });
        }
    }

    public static void updatePlace(final Lugar newLugar, final String key, Uri image) {
        if (image == null) {
            newLugar.setImagen(DEFAULT_URL);
            updatePlace(newLugar, key);
        } else {
            uploadImage(newLugar.getImagen(), image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        updatePlace(newLugar, key);
                    } else {
                        Toast.makeText(applicationContext, "No se ha podido subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void loadIntoImageView(StorageReference reference, final ImageView imageView) {
        reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Picasso.get().load(task.getResult()).into(imageView);
                } else {
                    Toast.makeText(applicationContext, "No se ha podido descargar la imagen", Toast.LENGTH_SHORT).show();
                    loadDefaultIntoImageView(imageView);
                }
            }
        });
    }

    private static void loadDefaultIntoImageView(final ImageView imageView) {
        DEFAULT_IMAGE.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Picasso.get().load(task.getResult()).into(imageView);
            }
        });
    }

    public static Task<UploadTask.TaskSnapshot> uploadImage(String reference, Uri file) {
        return FirebaseStorage.getInstance().getReference(reference).putFile(file);
    }

    public static String newId(){
        StringBuilder builder = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 15; i++){
            char c = (char) r.nextInt(50);
            builder.append(c);
        }
        return builder.toString();
    }
}
