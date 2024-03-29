package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoApi;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {
        var menu = """
                
                ************* Consulta Tabela Fip *************
                
                OPÇÕES:
                Carro | Moto | Caminhão
                
                Digite uma das opções de veículos para consulta:                
                """;

        System.out.println(menu);
        var opcao = leitura.nextLine();
        String endereco;

        if(opcao.toLowerCase().contains("carr")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        var marcas = conversor.obterLista(json, Dados.class);
        System.out.printf("\n| %-16s | %-3s |\n", "Marca", "Código");
        System.out.println("+------------------+--------+");
        marcas.stream()
                .sorted(Comparator.comparing(Dados::getNome))
                .forEach(dados -> {
                    System.out.printf("| %-16s | %-6s |\n", dados.getNome(), dados.getCodigo());
                });

        System.out.println("\nInforme o código da marca para consulta.");
        var codigoMarca = leitura.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        Optional<String> nomeDaMarca = conversor.getNomeDaMarcaPorCodigo(marcas, codigoMarca);

        System.out.printf("\n| %-3s | Modelos da marca: %-25s |\n","Código", nomeDaMarca.get());
        System.out.println("+--------+---------------------------------------------+");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::getNome))
                .forEach(dados -> {
                    System.out.printf("| %-6s | %-43s | \n", dados.getCodigo(), dados.getNome());
                });

        System.out.println("\nDigite um trecho do modelo a ser buscado");
        var modeloVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(modeloVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.printf("\n| %-3s | Modelos filtrados: %-24s |\n","Código","");
        System.out.println("+--------+---------------------------------------------+");
        modelosFiltrados.stream()
                .sorted(Comparator.comparing(Dados::getNome))
                .forEach(dados -> {
                    System.out.printf("| %-6s | %-43s | \n", dados.getCodigo(), dados.getNome());
                });

        System.out.println("\nDigite o código do modelo para buscar os valores de avaliação.");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.printf("\n| %-16s | %-43s | %-4s | %-15s | %-15s |\n", "Marca", "Modelo", "Ano", "Tipo Combustível", "Valor");
        System.out.println("+------------------+---------------------------------------------+------+------------------+-----------------+");
        veiculos.stream()
                .sorted(Comparator.comparing(Veiculo::marca)
                        .thenComparing(Veiculo::modelo)
                        .thenComparing(Veiculo::ano).reversed())
                .forEach(veiculo -> {
                    System.out.printf("| %-16s | %-43s | %-4s | %-16s | %-15s |\n",
                            veiculo.marca(), veiculo.modelo(), veiculo.ano(), veiculo.tipoCombustivel(), veiculo.valor());
                });
    }
}
