package com.example.ciudades;

import com.google.firebase.firestore.CollectionReference;

public class Usuario {
    String apellido;
    String nombre;

    public Usuario(String apellido, String nombre) {
        this.apellido = apellido;
        this.nombre = nombre;
    }

    public Usuario() {
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
