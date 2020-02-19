package com.example.ciudades;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ciudades.pojo.Ciudad;
import com.example.ciudades.pojo.Lugar;
import com.example.ciudades.pojo.LugarDestacado;
import com.example.ciudades.pojo.Usuario;
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
    public static Uri DEFAULT_IMAGE_RESOURCE;
    public static FirebaseUser user;
    public static DocumentReference userDocument;
    public static CollectionReference cityCollection;
    public static DocumentReference cityDocument;
    public static CollectionReference placeCollection;
    public static DocumentReference placeDocument;
    public static CollectionReference mainPlaceCollection;
    public static DocumentReference mainPlaceDocument;
    public static Context applicationContext;

    public static void initializeReferences() {
        DEFAULT_IMAGE.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                DEFAULT_IMAGE_RESOURCE = task.getResult();
            }
        });
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
                Toast.makeText(applicationContext, "No se ha podido borrar el lugar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void updateUser(Usuario s) {
        userDocument.set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(applicationContext, "Se ha actualizado el usuario", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(applicationContext, "No se ha podido actualizar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void updateUser(final Usuario s, Uri image) {
        if (image == null) {
            s.setImagen(DEFAULT_URL);
            updateUser(s);
        } else {
            uploadImage(s.getImagen(), image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        updateUser(s);
                    }
                }
            });
        }
    }

    public static void addCity(final Ciudad c, final Uri image) {
        final String randomString = getRandomString();
        c.setImagen(randomString);
        if (image == null) {
            uploadImage(randomString, DEFAULT_IMAGE_RESOURCE).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    cityCollection.document(randomString).set(c);
                }
            });
        } else {
            uploadImage(randomString, image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    cityCollection.document(randomString).set(c);
                }
            });
        }
    }

    public static void addPlace(final Lugar lugar, final Uri image) {
        crearRecursivamente(false);
        final String randomString = getRandomString();
        lugar.setImagen(randomString);
        if (image == null) {
            uploadImage(randomString, DEFAULT_IMAGE_RESOURCE).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    placeCollection.document(randomString).set(lugar);
                }
            });
        } else {
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
        int i = 0;
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
                    updateCity(newCity, key);
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
                    updatePlace(newLugar, key);
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

    public static String newId() {
        StringBuilder builder = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 15; i++) {
            int c = r.nextInt(50);
            builder.append(c);
        }
        return builder.toString();
    }

    public static void addMainPlace(final LugarDestacado lugar, final Uri image) {
        crearRecursivamente(true);
        final String randomString = getRandomString();
        lugar.setImage(randomString);
        if (image == null) {
            uploadImage(randomString, DEFAULT_IMAGE_RESOURCE).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    mainPlaceCollection.document(randomString).set(lugar);
                }
            });
        } else {
            uploadImage(randomString, image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    mainPlaceCollection.document(randomString).set(lugar);
                }
            });
        }
    }

    public static void updateMainPlace(final LugarDestacado newLugar, final String key, Uri image) {
        if (image == null) {
            newLugar.setImage(DEFAULT_URL);
            updateMainPlace(newLugar, key);
        } else {
            uploadImage(newLugar.getImage(), image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    updateMainPlace(newLugar, key);
                }
            });
        }
    }

    private static void updateMainPlace(LugarDestacado newLugar, String key) {
        mainPlaceCollection.document(key).set(newLugar).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(applicationContext, "No se ha podido actualizar la ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteMainPlace(String key) {
        mainPlaceCollection.document(key).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(applicationContext, "No se ha podido borrar el lugar destacado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void crearRecursivamente(boolean includePlace) {
        if (includePlace) {
            placeDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.getResult().exists()) {
                        placeDocument.set(new Lugar("Lugar", "Descripción", placeDocument.getId()));
                    }
                }
            });
        }
        cityDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()) {
                    cityDocument.set(new Ciudad("Ciudad", "País", cityDocument.getId()));
                }
            }
        });
    }

    public enum Accion {ADD_REQUEST, ADD_ACTION, EDIT_REQUEST, EDIT_ACTION, DELETE}
}
