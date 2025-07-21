package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final List<Result> resultados = new ArrayList<>();

    private static Random random;

    public static void main(String[] args) {
        int[] tamanhos = {100000, 500000, 1000000, 5000000, 10000000};
        int[] tamanhosPadroes = {8, 10, 12, 15};
        int testesporConfig = 10;

        System.out.println("*** ALGORITMO DE FORÇA BRUTA ***");

        for(int tamanhoBase : tamanhos){
            for(int tamanhoProcurada: tamanhosPadroes){
                for(int teste = 0; teste < testesporConfig; teste++){
                    random = new Random(42 + teste);
                    String cadeiaBase = gerarCadeiaAleatoria(tamanhoBase);
                    String cadeiraProcurada = gerarCadeiaAleatoria(tamanhoProcurada);
                    algoritmoForcaBruta(cadeiaBase, cadeiraProcurada);
                }
            }
        }

        resultados.forEach(result -> {
            System.out.println("======================================");
            System.out.println("Resultado " + (resultados.indexOf(result) + 1));
            System.out.println("Tamanho da instância base: " + result.cadeiaBase.length());
            System.out.println("Tamanho da instância procurada: " + result.cadeiaProcurada.length());
            if(result.casaCasamento >= 0) System.out.println("Casamento na casa " + result.casaCasamento);
            else System.out.println("Não houve casamento");
            System.out.printf("Tempo de execução: %.2f ms\n", result.tempoExecucao);
            System.out.println("Número de comparações: " + result.comparacoes);
        });

        gerarCSV();
    }

    public static void algoritmoForcaBruta(String cadeia_base, String cadeia_procurada){
        Result result = new Result();
        long inicio = System.nanoTime();
        result.cadeiaProcurada = cadeia_procurada;
        result.cadeiaBase = cadeia_base;
        resultados.add(result);
        int n = cadeia_base.length();
        int m = cadeia_procurada.length();
        for(int i = 0; i <= n - m; i++){
            for(int j = 0; j < m; j++){
                result.comparacoes += 1;
                if(cadeia_base.charAt(i + j) != cadeia_procurada.charAt(j)){
                    break;
                }
                if(j == m-1){
                    result.casaCasamento = i;
                    long fim = System.nanoTime();
                    result.tempoExecucao = (fim - inicio) / 1_000_000.0;
                    return;
                }
            }
        }
        result.casaCasamento = -1;
        long fim = System.nanoTime();
        result.tempoExecucao = (fim - inicio) / 1_000_000.0;
    }

    public static void gerarCSV() {
        String nomeArquivo = "resultados_forca_bruta.csv";

        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            // Cabeçalho do CSV
            writer.append("Teste,Tamanho_Base,Tamanho_Procurada,Casa_Casamento,Tempo_Execucao_ms,Numero_Comparacoes,Houve_Casamento\n");

            // Dados
            for (int i = 0; i < resultados.size(); i++) {
                Result result = resultados.get(i);
                writer.append(String.valueOf(i + 1)).append(",");
                writer.append(String.valueOf(result.cadeiaBase.length())).append(",");
                writer.append(String.valueOf(result.cadeiaProcurada.length())).append(",");
                writer.append(String.valueOf(result.casaCasamento)).append(",");
                writer.append(String.format("%.2f", result.tempoExecucao)).append(",");
                writer.append(String.valueOf(result.comparacoes)).append(",");
                writer.append(result.casaCasamento >= 0 ? "Sim" : "Não").append("\n");
            }

            System.out.println("\n*** CSV GERADO COM SUCESSO ***");
            System.out.println("Arquivo salvo como: " + nomeArquivo);
            System.out.println("Total de registros: " + resultados.size());

        } catch (IOException e) {
            System.err.println("Erro ao gerar o arquivo CSV: " + e.getMessage());
        }
    }

    public static String gerarCadeiaAleatoria(int tamanho) {
        char[] bases = {'A', 'T', 'C', 'G'};
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tamanho; i++) {
            int indice = random.nextInt(bases.length);
            sb.append(bases[indice]);
        }

        return sb.toString();
    }
}