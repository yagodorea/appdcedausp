package com.dceusp.appdcedausp.utils;

import java.io.Serializable;


public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    private String titulo;
    private String descricao;
    private String autor;
    private String imagem;
    private long criadoem;
    private boolean aprovado;

    public Post() {}

    public Post(String title, String description, String author, String imageUrl, long created, boolean aprov) {
        titulo = title;
        descricao = description;
        autor = author;
        imagem = imageUrl;
        criadoem = created;
        aprovado = aprov;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getAutor() {
        return autor;
    }

    public String getImagem() {
        return imagem;
    }

    public long getCriadoem() {
        return criadoem;
    }

    public boolean getAprovado() {
        return aprovado;
    }
}
