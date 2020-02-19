package com.example.ciudades.pojo;

public class Ciudad {
    private String ciudad;
    private String pais;
    private String imagen;

    public Ciudad(String ciudad, String pais, String imagen) {
        this.ciudad = ciudad;
        this.pais = pais;
        this.imagen = imagen;
    }

    public Ciudad() {
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
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
