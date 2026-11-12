/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import org.bson.Document;

public class Usuario {

    private String codigo;
    private String nombreCompleto;
    private String documento;
    private String contacto;
    private boolean activo;

    public Usuario() {
        this.activo = true;
    }

    public Usuario(String codigo, String nombreCompleto, String documento, String contacto, boolean activo) {
        this.codigo = codigo;
        this.nombreCompleto = nombreCompleto;
        this.documento = documento;
        this.contacto = contacto;
        this.activo = activo;
    }

    public Document toDocument() {
        return new Document("codigo", codigo)
                .append("nombreCompleto", nombreCompleto)
                .append("documento", documento)
                .append("contacto", contacto)
                .append("activo", activo);
    }

    public static Usuario fromDocument(Document doc) {
        if (doc == null) {
            return null;
        }
        return new Usuario(
                doc.getString("codigo"),
                doc.getString("nombreCompleto"),
                doc.getString("documento"),
                doc.getString("contacto"),
                doc.getBoolean("activo", true)
        );
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}