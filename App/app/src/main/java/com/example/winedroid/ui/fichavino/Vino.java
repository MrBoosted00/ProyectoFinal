package com.example.winedroid.ui.fichavino;

import java.io.Serializable;
import java.util.ArrayList;

public class Vino implements Serializable {
    String nombre;
    String descripcion;
    String imagen;
    Integer valoracion;
    String denominacion;
    ArrayList<Comentario> listaComentarios = new ArrayList<Comentario>();

    public Vino(String nombre, String descripcion, String imagen, Integer valoracion, String denominacion, ArrayList<Comentario> listaComentarios) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.valoracion = valoracion;
        this.denominacion = denominacion;
        this.listaComentarios = listaComentarios;
    }

    public Vino(String nombre, String descripcion, String imagen, Integer valoracion, String denominacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.valoracion = valoracion;
        this.denominacion = denominacion;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public ArrayList<Comentario> getListaComentarios() {
        return listaComentarios;
    }

    public void setListaComentarios(ArrayList<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }

    public void añadirComentario(Comentario c){
        this.listaComentarios.add(c);
    }
}
