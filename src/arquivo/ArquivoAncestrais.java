package arquivo;

import componentes.Ancestral;
import componentes.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class ArquivoAncestrais extends ArquivoUtil{

    public synchronized Ancestral getAncestral (String codigo) {

        Ancestral ancestral = null;

        FileInputStream entrada = null;
        InputStreamReader leitor = null;
        BufferedReader buffer_entrada = null;

        try {
            File file = new File(ARQUIVO_ANC);
            entrada = new FileInputStream(file);
            leitor = new InputStreamReader(entrada);
            buffer_entrada = new BufferedReader(leitor);

            String linha;

            JSONParser parser = new JSONParser();
            Object obj; JSONObject jsonObject;
            String cod; int filhos;

            while ((linha = buffer_entrada.readLine()) != null) {
                if(!linha.equals("")) {

                    obj = parser.parse(linha);
                    jsonObject = (JSONObject) obj;

                    cod = (String) jsonObject.get("codigo");
                    filhos = Integer.valueOf((String) jsonObject.get("filhos"));

                    if (codigo.equals(cod)) {
                        ancestral = new Ancestral(filhos, cod);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            finalizarArquivos(entrada, leitor, buffer_entrada);
        }

        return ancestral;
    }

    public synchronized boolean addFilho (String codigo) {
        boolean encontrou = false;
        JSONParser parser = new JSONParser();

        File arquivo;
        FileInputStream entrada = null;
        InputStreamReader leitor = null;
        BufferedReader buffer_entrada = null;

        try {
            arquivo = new File (ARQUIVO_ANC);
            entrada = new FileInputStream (arquivo);
            leitor = new InputStreamReader (entrada);
            buffer_entrada = new BufferedReader (leitor);

            deletarArquivo(ARQUIVO_TMP2);
            File arqTemp = new File (ARQUIVO_TMP2);
            FileOutputStream saida_temp = new FileOutputStream (arqTemp, true);
            OutputStreamWriter gravador_temp = new OutputStreamWriter (saida_temp);
            BufferedWriter buffer_saida_temp = new BufferedWriter (gravador_temp);

            String linha;
            Object obj; JSONObject jsonObject; String codAncestral;

            while ((linha = buffer_entrada.readLine()) != null) {

                linha = linha.replace(SEPARATOR, "");
                obj = parser.parse(linha);
                jsonObject = (JSONObject) obj;

                codAncestral = (String) jsonObject.get("codigo");
                int filAncestral = Integer.valueOf((String) jsonObject.get("filhos"));

                if(codAncestral.equals(codigo)) {
                    filAncestral++;
                    Ancestral ancestral = new Ancestral(filAncestral, codAncestral);
                    linha = ancestral.toJson();
                    encontrou = true;
                }
                buffer_saida_temp.write(linha + SEPARATOR);
            }

            if (!encontrou) {
                Ancestral ancestral = new Ancestral(1, codigo);
                buffer_saida_temp.write(ancestral.toJson() + SEPARATOR);
            }

            buffer_saida_temp.flush();
            saida_temp.close();
            gravador_temp.close();
            buffer_saida_temp.close();

            entrada.close();
            leitor.close();
            buffer_entrada.close();

            File oldfile = new File(ARQUIVO_TMP2);
            File newfile = new File(ARQUIVO_ANC);

            deletarArquivo(ARQUIVO_ANC);
            oldfile.renameTo(newfile);

            encontrou = true;

        } catch (Exception e) {
            System.err.println ("Erro ao atualizar o arquivo. Erro: " + e.getMessage());
            e.printStackTrace();
        }

        return encontrou;
    }

    public synchronized ObservableList<Ancestral> lerAncestrais () {

        ObservableList<Ancestral> list = FXCollections.observableArrayList();
        File arq;
        FileInputStream entrada = null;
        InputStreamReader leitor = null;
        BufferedReader buffer_entrada = null;
        ArquivoRequest arquivo = new ArquivoRequest();

        try {
            arq = new File(ARQUIVO_ANC);
            entrada = new FileInputStream(arq);
            leitor = new InputStreamReader(entrada);
            buffer_entrada = new BufferedReader(leitor);

            String linha; JSONParser parser; Object obj; JSONObject jsonObject;
            linha = buffer_entrada.readLine();
            Ancestral ancestral;

            while (linha != null) {
                if(!linha.equals("")) {
                    linha = linha.replace(SEPARATOR, "");
                    parser = new JSONParser();
                    obj = parser.parse(linha);
                    jsonObject = (JSONObject) obj;

                    String codigo = (String) jsonObject.get("codigo");
                    Integer filhos = Integer.valueOf((String) jsonObject.get("filhos"));
                    Request rq = arquivo.buscarInformacoesCodigo(codigo);

                    if (rq != null) {
                        ancestral = new Ancestral(rq.getLink(), filhos);
                        list.add(ancestral);
                    }
                }
                linha = buffer_entrada.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            finalizarArquivos(entrada, leitor, buffer_entrada);
        }

        return list;
    }


    public synchronized int getFilhos (String codigo) {
        return getFilhos(codigo, ARQUIVO_VIS) + getFilhos(codigo, ARQUIVO);
    }

    public synchronized int getFilhos (String codigo, String arquivo) {

        FileInputStream entrada = null;
        InputStreamReader leitor = null;
        BufferedReader buffer_entrada = null;

        int filhos = 0;

        try {
            File file = new File(arquivo);
            entrada = new FileInputStream(file);
            leitor = new InputStreamReader(entrada);
            buffer_entrada = new BufferedReader(leitor);

            String linha;

            JSONParser parser = new JSONParser();
            Object obj; JSONObject jsonObject;
            String origem;

            while ((linha = buffer_entrada.readLine()) != null) {
                if(!linha.equals("")) {

                    obj = parser.parse(linha);
                    jsonObject = (JSONObject) obj;

                    origem = (String) jsonObject.get("origem");

                    if (codigo.equals(origem)) {
                        filhos++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            finalizarArquivos(entrada, leitor, buffer_entrada);
        }

        return filhos;
    }

}
