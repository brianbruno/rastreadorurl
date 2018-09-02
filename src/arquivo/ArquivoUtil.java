package arquivo;

import java.io.*;

public abstract class ArquivoUtil {

    public static final String SEPARATOR = System.getProperty ("line.separator");
    public static final String ARQUIVO = "files/request.json";
    public static final String ARQUIVO_LOG = "files/log.txt";
    public static final String ARQUIVO_VIS = "files/url_visitada.json";
    public static final String ARQUIVO_TMP = "files/arquivo_temp.json";
    public static final String ARQUIVO_TMP2 = "files/arquivo_temp2.json";
    public static final String ARQUIVO_ANC = "files/ancestrais.json";

    public synchronized boolean deletarArquivo (String nome) {
        boolean resultado = false;

        try {
            File arq = new File(nome);
            resultado = arq.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public void finalizarArquivos(FileInputStream entrada, InputStreamReader leitor, BufferedReader buffer_entrada) {
        try {
            if (entrada != null)
                entrada.close();
            if (leitor != null)
                leitor.close();
            if (buffer_entrada != null)
                buffer_entrada.close();

        } catch (Exception ex) {
            System.err.println("Erro ao finalizar os arquivos.");
        }
    }

    public void finalizarArquivos(FileOutputStream saida, OutputStreamWriter gravador, BufferedWriter buffer_saida) {
        try {
            if (saida != null) {
                saida.close();
            }
            if (gravador != null) {
                gravador.close();
            }
            if (buffer_saida != null) {
                buffer_saida.close();
            }
        } catch (Exception ex) {
            System.err.println("Erro ao finalizar os arquivos.");
        }
    }
}
