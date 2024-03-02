package br.com.alura.TabelaFipe.service;

import java.util.List;
import java.util.Optional;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);

    <T> List<T> obterLista(String json, Class<T> classe);

    <T> Optional<String> getNomeDaMarcaPorCodigo(List<T> dados, String codigoDesejado);
}

