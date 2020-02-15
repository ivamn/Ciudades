package com.example.ciudades;

public class LugarDestacado {
    private String image;
    private String nombre;
    private String descripcion;

    public LugarDestacado() {
    }

    public LugarDestacado(String image, String nombre, String descripcion) {
        this.image = image;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
