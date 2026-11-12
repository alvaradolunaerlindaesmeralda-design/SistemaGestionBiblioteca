 package modelo;

import modelo.Material;

public class Libro extends Material {

    private String autor;
    private String editorial;

    public Libro(String codigo, String titulo, int anioPublicacion,
                 String autor, String editorial) {

        super(codigo, titulo, anioPublicacion);

        this.autor = autor;
        this.editorial = editorial;
    }

    public String getAutor() {
        return autor;
    }

    public String getEditorial() {
        return editorial;
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Autor: " + autor);
        System.out.println("Editorial: " + editorial);
    }
}