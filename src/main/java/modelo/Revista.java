package modelo;

import modelo.Material;

public class Revista extends Material {

    private int numeroEdicion;
    private String periodicidad;

    public Revista(String codigo, String titulo, int anioPublicacion,
                   int numeroEdicion, String periodicidad) {

        super(codigo, titulo, anioPublicacion);

        this.numeroEdicion = numeroEdicion;
        this.periodicidad = periodicidad;
    }

    public int getNumeroEdicion() {
        return numeroEdicion;
    }

    public String getPeriodicidad() {
        return periodicidad;
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Número de edición: " + numeroEdicion);
        System.out.println("Periodicidad: " + periodicidad);
    }
}