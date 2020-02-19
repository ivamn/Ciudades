package com.example.ciudades.pojo;

public class Lugar {
    String lugar;
    String descripcion;
    String imagen;

    public Lugar() {
    }

    public Lugar(String lugar, String descripcion, String imagen) {
        this.lugar = lugar;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}
