package com.example.ciudades;

public class Usuario {
    private String apellido;
    private String nombre;
    private String imagen;

    public Usuario(String apellido, String nombre, String imagen) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public Usuario() {
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
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
