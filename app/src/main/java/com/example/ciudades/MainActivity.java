package com.example.ciudades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText usuario, pass;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = findViewById(R.id.editUsuario);
        pass = findViewById(R.id.editPass);
        Button crear = findViewById(R.id.buttonCrear);
        Button entrar = findViewById(R.id.buttonEntrar);

        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Operations.user = firebaseAuth.getCurrentUser();
                if (Operations.user != null) {
                    Toast.makeText(MainActivity.this, Operations.user.getEmail() + " logeado", Toast.LENGTH_LONG).show();
                    auth = firebaseAuth;
                }
            }
        };

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.createUserWithEmailAndPassword(usuario.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    iniciarAplicacion();
                                } else {
                                    Toast.makeText(MainActivity.this, "Problemas al crear el usuario", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signInWithEmailAndPassword(usuario.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    iniciarAplicacion();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error de autenticación", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

    }

    private void iniciarAplicacion() {
        Intent intent = new Intent(this, MainApplication.class);
        Operations.initializeReferences();
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (auth != null) {
            auth.removeAuthStateListener(authListener);
            auth.signOut();
        }
    }
}