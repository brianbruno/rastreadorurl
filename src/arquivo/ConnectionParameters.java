package arquivo;

import database.ConnectionController;
import exception.ParametroNaoEncontrado;
import javafx.scene.control.TextInputDialog;
import util.ScreenService;

import java.io.*;

public class ConnectionParameters {

    private static String arquivo = "rastreador.config";
    private static int tentativas = 0;

    public ConnectionController startConnection() {
        String dburl = getParameter("DBURL");
        String username = getParameter("USERNAME");
        String password = getParameter("PASSWORD");

        ConnectionController cc = null;

        if (dburl != null && username != null && password != null) {
            cc = new ConnectionController(dburl, username, password);
            boolean conect = cc.conectar();
            if (!conect) {
                cc = null;
            }
        }

        return cc;
    }

    public static String getParameter (String parametro) {

        FileInputStream entrada = null;
        InputStreamReader leitor = null;
        BufferedReader buffer_entrada = null;
        String resultado = null;

        try {
            File file = new File(arquivo);
            entrada = new FileInputStream(file);
            leitor = new InputStreamReader(entrada);
            buffer_entrada = new BufferedReader(leitor);

            String linha;

            while ((linha = buffer_entrada.readLine()) != null) {
                if(!linha.equals("")) {
                    String[] info = linha.split("=");

                    if (info[0].equals(parametro)) {
                        resultado = info[1];
                    }
                }
            }

            if (resultado == null) {
                throw new ParametroNaoEncontrado(parametro);
            }

        } catch (ParametroNaoEncontrado pnr) {
            ScreenService.showStackTrace(pnr);
        } catch (FileNotFoundException exception) {
            exibirMensagemDiretorioArquivo();
            resultado = getParameter(parametro);
        } catch (Exception e) {
            ScreenService.showStackTrace(e);
        }  finally {
            finalizarArquivos(entrada, leitor, buffer_entrada);
        }

        return resultado;
    }

    private static void exibirMensagemDiretorioArquivo() {

        if (tentativas < 3) {
            TextInputDialog dialogoNome = new TextInputDialog();

            dialogoNome.setTitle("Arquivo de configuração");
            dialogoNome.setHeaderText("Arquivo de configuração não encontrado.");
            dialogoNome.setContentText("Digite abaixo o diretorio do arquivo:");

            // se o usuário fornecer um valor, assignamos ao nome
            dialogoNome.showAndWait().ifPresent(v -> arquivo = v);

            if(!dialogoNome.showAndWait().isPresent())
                System.exit(0);

            tentativas++;
        }
    }

    public static void finalizarArquivos(FileInputStream entrada, InputStreamReader leitor, BufferedReader buffer_entrada) {
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

    public static void finalizarArquivos(FileOutputStream saida, OutputStreamWriter gravador, BufferedWriter buffer_saida) {
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
