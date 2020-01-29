package com.example.ciudades;

class Ciudad {
    private String nombre;
    private String pais;
    private String imagen;

    public Ciudad(String nombre, String pais, String imagen) {
        this.nombre = nombre;
        this.pais = pais;
        this.imagen = imagen;
    }

    public Ciudad() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
