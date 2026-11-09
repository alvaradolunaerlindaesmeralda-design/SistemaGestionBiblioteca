 package com.biblioteca.sistemagestionbiblioteca;

public class MaterialServicio {

    private final MaterialDAO materialDAO;

    public MaterialServicio() {
        materialDAO = new MaterialDAO();
    }

    public void registrarLibro(Libro libro) {

        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo.");
        }

        if (libro.getCodigo() == null || libro.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }

        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }

        materialDAO.registrarLibro(libro);
    }

    public void registrarRevista(Revista revista) {

        if (revista == null) {
            throw new IllegalArgumentException("La revista no puede ser nula.");
        }

        if (revista.getCodigo() == null || revista.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }

        if (revista.getTitulo() == null || revista.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }

        materialDAO.registrarRevista(revista);
    }
}