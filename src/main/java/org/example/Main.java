package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final List<Result> resultadosForcaBruta = new ArrayList<>();
    private static final List<Result> resultadosKMP = new ArrayList<>();

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

        System.out.println("\n*** ALGORITMO KMP ***");

        for(int tamanhoBase : tamanhos){
            for(int tamanhoProcurada: tamanhosPadroes){
                for(int teste = 0; teste < testesporConfig; teste++){
                    random = new Random(42 + teste);
                    String cadeiaBase = gerarCadeiaAleatoria(tamanhoBase);
                    String cadeiraProcurada = gerarCadeiaAleatoria(tamanhoProcurada);
                    algoritmoKMP(cadeiaBase, cadeiraProcurada);
                }
            }
        }

        // Exibir resultados Força Bruta
        System.out.println("\n=== RESULTADOS FORÇA BRUTA ===");
        resultadosForcaBruta.forEach(result -> {
            System.out.println("======================================");
            System.out.println("Resultado " + (resultadosForcaBruta.indexOf(result) + 1));
            System.out.println("Tamanho da instância base: " + result.cadeiaBase.length());
            System.out.println("Tamanho da instância procurada: " + result.cadeiaProcurada.length());
            if(result.casaCasamento >= 0) System.out.println("Casamento na casa " + result.casaCasamento);
            else System.out.println("Não houve casamento");
            System.out.printf("Tempo de execução: %.2f ms\n", result.tempoExecucao);
            System.out.println("Número de comparações: " + result.comparacoes);
        });

        // Exibir resultados KMP
        System.out.println("\n=== RESULTADOS KMP ===");
        resultadosKMP.forEach(result -> {
            System.out.println("======================================");
            System.out.println("Resultado " + (resultadosKMP.indexOf(result) + 1));
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
        result.algoritmo = "Força Bruta";
        resultadosForcaBruta.add(result);
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

    public static int[] calcularFuncaoFalha(String p, Result result){
        int m = p.length();
        int[] f = new int[m];
        f[0] = 0;
        int k = 0;

        for(int i = 1; i < m; i++){
            while(k > 0 && (p.charAt(k) != p.charAt(i))){
                result.comparacoes += 1; // Comparação durante construção da função de falha
                k = f[k-1];
            }

            result.comparacoes += 1; // Comparação p.charAt(k) == p.charAt(i)
            if(p.charAt(k) == p.charAt(i)) k += 1;

            f[i] = k;
        }

        return f;
    }

    public static void algoritmoKMP(String cadeia_base, String cadeia_procurada){
        Result result = new Result();
        long inicio = System.nanoTime();
        result.cadeiaProcurada = cadeia_procurada;
        result.cadeiaBase = cadeia_base;
        result.algoritmo = "KMP";
        resultadosKMP.add(result);

        int n = cadeia_base.length();
        int m = cadeia_procurada.length();
        int[] f = calcularFuncaoFalha(cadeia_procurada, result);

        int q = 0;

        for(int i = 0; i < n; i++){
            while(q > 0 && cadeia_procurada.charAt(q) != cadeia_base.charAt(i)){
                result.comparacoes += 1; // Comparação que falhou
                q = f[q-1];
            }

            result.comparacoes += 1; // Comparação cadeia_procurada.charAt(q) == cadeia_base.charAt(i)
            if(cadeia_procurada.charAt(q) == cadeia_base.charAt(i)) q += 1;

            if(q == m){
                result.casaCasamento = i - m + 1;
                long fim = System.nanoTime();
                result.tempoExecucao = (fim - inicio) / 1_000_000.0;
                return; // Para no primeiro casamento, igual ao força bruta
            }
        }

        result.casaCasamento = -1;
        long fim = System.nanoTime();
        result.tempoExecucao = (fim - inicio) / 1_000_000.0;
    }

    public static void gerarCSV() {
        String nomeArquivo = "resultados_comparacao.csv";

        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            // Cabeçalho do CSV
            writer.append("Teste;Algoritmo;Tamanho_Base;Tamanho_Procurada;Casa_Casamento;Tempo_Execucao_ms;Numero_Comparacoes;Houve_Casamento\n");

            // Dados Força Bruta
            for (int i = 0; i < resultadosForcaBruta.size(); i++) {
                Result result = resultadosForcaBruta.get(i);
                writer.append(String.valueOf(i + 1)).append(";");
                writer.append(result.algoritmo).append(";");
                writer.append(String.valueOf(result.cadeiaBase.length())).append(";");
                writer.append(String.valueOf(result.cadeiaProcurada.length())).append(";");
                writer.append(String.valueOf(result.casaCasamento)).append(";");
                writer.append(String.format("%.2f", result.tempoExecucao)).append(";");
                writer.append(String.valueOf(result.comparacoes)).append(";");
                writer.append(result.casaCasamento >= 0 ? "Sim" : "Não").append("\n");
            }

            // Dados KMP
            for (int i = 0; i < resultadosKMP.size(); i++) {
                Result result = resultadosKMP.get(i);
                writer.append(String.valueOf(i + 1 + resultadosForcaBruta.size())).append(";");
                writer.append(result.algoritmo).append(";");
                writer.append(String.valueOf(result.cadeiaBase.length())).append(";");
                writer.append(String.valueOf(result.cadeiaProcurada.length())).append(";");
                writer.append(String.valueOf(result.casaCasamento)).append(";");
                writer.append(String.format("%.2f", result.tempoExecucao)).append(";");
                writer.append(String.valueOf(result.comparacoes)).append(";");
                writer.append(result.casaCasamento >= 0 ? "Sim" : "Não").append("\n");
            }

            System.out.println("\n*** CSV GERADO COM SUCESSO ***");
            System.out.println("Arquivo salvo como: " + nomeArquivo);
            System.out.println("Total de registros: " + (resultadosForcaBruta.size() + resultadosKMP.size()));

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