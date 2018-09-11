package arquivo;

import componentes.Request;
import database.ConnectionController;
import javafx.scene.control.TextInputDialog;

import java.io.*;

public class ConnectionParameters extends ArquivoUtil{

    private String arquivo = "rastreador.configa";
    private int tentativas = 0;

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

    public String getParameter  (String parametro) {

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

        } catch (FileNotFoundException exception) {
            exibirMensagemDiretorioArquivo();
            resultado = getParameter(parametro);
        } catch (Exception e) {
            System.err.println("Erro ao buscar o parametro: " + parametro);
        }  finally {
            finalizarArquivos(entrada, leitor, buffer_entrada);
        }

        return resultado;
    }

    private void exibirMensagemDiretorioArquivo() {

        if (tentativas < 3) {
            TextInputDialog dialogoNome = new TextInputDialog();

            dialogoNome.setTitle("Arquivo de configuração");
            dialogoNome.setHeaderText("Arquivo de configuração não encontrado.");
            dialogoNome.setContentText("Digite abaixo o diretorio do arquivo:");

            // se o usuário fornecer um valor, assignamos ao nome
            dialogoNome.showAndWait().ifPresent(v -> arquivo = v);

            tentativas++;
        }
    }

}
