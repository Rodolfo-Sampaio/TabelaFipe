package br.com.alura.TabelaFipe.model;

public record Dados (String codigo, String nome){
    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }
}
