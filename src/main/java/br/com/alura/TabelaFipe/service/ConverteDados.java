package br.com.alura.TabelaFipe.service;

import br.com.alura.TabelaFipe.model.Dados;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;
import java.util.Optional;

public class ConverteDados implements IConverteDados{
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> obterLista(String json, Class<T> classe) {
        CollectionType lista = mapper.getTypeFactory()
                .constructCollectionType(List.class, classe);
        try {
            return mapper.readValue(json, lista);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Optional<String> getNomeDaMarcaPorCodigo(List<T> dados, String codigoDesejado) {
        return dados.stream()
                .filter(dado -> dado instanceof Dados)
                .map(dado -> (Dados) dado)
                .filter(dado -> dado.getCodigo().equals(codigoDesejado))
                .map(Dados::getNome)
                .findFirst();
    }
}
