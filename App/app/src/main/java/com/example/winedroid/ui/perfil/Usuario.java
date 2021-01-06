package com.example.winedroid.ui.perfil;

import java.io.Serializable;

public class Usuario implements Serializable {
    String nick;
    String descripcion;
    String fotoPerfil;

    public Usuario() {

    }

    public Usuario(String nickname, String descripcion,String fotoPerfil) {
        this.nick = nickname;
        this.descripcion = descripcion;
        this.fotoPerfil = fotoPerfil;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}
