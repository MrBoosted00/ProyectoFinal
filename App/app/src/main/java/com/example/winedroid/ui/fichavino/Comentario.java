package com.example.winedroid.ui.fichavino;

import java.io.Serializable;

public class Comentario implements Serializable {
    String uid;
    String comentario;
    Integer valoracion;

    public Comentario(String uid, String comentario, Integer valoracion) {
        this.uid = uid;
        this.comentario = comentario;
        this.valoracion = valoracion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }
}
