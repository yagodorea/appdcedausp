package com.dceusp.appdcedausp.utils;

public class Resposta {

    private String texto;
    private String autor;
    private long quando;

    public Resposta() {}

    public Resposta(String texto, String autor, long quando) {
        this.texto = texto;
        this.autor = autor;
        this.quando = quando;
    }

    public String getTexto() {
        return texto;
    }

    public String getAutor() {
        return autor;
    }

    public long getQuando() {
        return quando;
    }
}
