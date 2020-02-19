package com.example.ciudades.pojo;

public class Usuario {
    private String apellido;
    private String nombre;
    private String imagen;
    private String telefono;
    private boolean primera_vez;

    public Usuario(String apellido, String nombre, String imagen, String telefono, boolean primera_vez) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.imagen = imagen;
        this.telefono = telefono;
        this.primera_vez = primera_vez;
    }

    public Usuario() {
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isPrimera_vez() {
        return primera_vez;
    }

    public void setPrimera_vez(boolean primera_vez) {
        this.primera_vez = primera_vez;
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
