package arquivo;

import org.json.simple.JSONObject;

import java.io.*;
import componentes.Request;
import org.json.simple.parser.JSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArquivoRequest extends ArquivoUtil {

    private static FileOutputStream saida = null;
    private static OutputStreamWriter gravador = null;
    private static BufferedWriter buffer_saida = null;
    private static FileInputStream entrada = null;
    private static InputStreamReader leitor = null;
    private static BufferedReader buffer_entrada = null;
    private static File arq = null;

    private ArrayList<Request> todasRequests;

    public synchronized boolean gravarUrl (ArrayList<Request> requests) {
        boolean resultado = false;

        try {

            arq = new File(ARQUIVO);

            saida = new FileOutputStream(arq, true);
            gravador = new OutputStreamWriter(saida);
            buffer_saida = new BufferedWriter(gravador);

            requests.forEach((request) -> {
                try {
                    buffer_saida.write(request.toJson());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            buffer_saida.flush();

            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fecharManipuladoresEscrita();
        }


        return resultado;
    }

    public synchronized boolean marcarVisitado (Request item) {
        boolean resultado = false;

        File arq;
        FileOutputStream saida = null;
        OutputStreamWriter gravador = null;
        BufferedWriter buffer_saida = null;
        try {

            arq = new File(ARQUIVO_VIS);

            saida = new FileOutputStream(arq, true);
            gravador = new OutputStreamWriter(saida);
            buffer_saida = new BufferedWriter(gravador);

            buffer_saida.write(item.toJson());

            buffer_saida.flush();

            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            finalizarArquivos(saida, gravador, buffer_saida);
        }

        return resultado && removerUrl(item);
    }

    public synchronized boolean removerUrl (Request item) {
        boolean resultado = false;
        JSONParser parser = new JSONParser();

        try {
            File arqTemp = new File (ARQUIVO_TMP);
            File arq = new File (ARQUIVO);
            saida = new FileOutputStream (arqTemp, true);
            gravador = new OutputStreamWriter (saida);
            buffer_saida = new BufferedWriter (gravador);

            entrada = new FileInputStream (arq);
            leitor = new InputStreamReader (entrada);
            buffer_entrada = new BufferedReader (leitor);
            String linha;

            while ((linha = buffer_entrada.readLine()) != null) {
                Object obj = parser.parse(linha.replace(SEPARATOR, ""));
                JSONObject jsonObject = (JSONObject) obj;
                String codigo = (String)  jsonObject.get("codigo");

                if (codigo != null && !codigo.equals("")) {
                    if (codigo.equals(item.getCodigo())) {
                        linha = buffer_entrada.readLine();
                        if (linha != null)
                            buffer_saida.write(linha + SEPARATOR);
                    } else {
                        buffer_saida.write(linha + SEPARATOR);
                        buffer_saida.flush();
                    }
                }
            }
            fecharManipuladoresEscrita();

            if(deletarArquivo(ARQUIVO)) {
                if (arqTemp.renameTo(new File(ARQUIVO)))
                    resultado = true;
            }

        } catch (Exception e) {
            System.err.println ("Erro ao atualizar o arquivo.");
            resultado = false;
            e.printStackTrace ();
        } finally {
            fecharManipuladoresEscrita();
        }

        return resultado;
    }

    public synchronized boolean gravarLog (String log) {
        boolean resultado = false;

        File arq;
        FileOutputStream saida = null;
        OutputStreamWriter gravador = null;
        BufferedWriter buffer_saida = null;

        try {

            arq = new File(ARQUIVO_LOG);

            saida = new FileOutputStream(arq, true);
            gravador = new OutputStreamWriter(saida);
            buffer_saida = new BufferedWriter(gravador);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String data = dateFormat.format(date); //2016/11/16 12:08:43

            try {
                buffer_saida.write("Data: " + data + SEPARATOR);
                buffer_saida.write(log + SEPARATOR);
                buffer_saida.write("-----------------------------------------------" + SEPARATOR);
            } catch (Exception e) {
                e.printStackTrace();
            }

            buffer_saida.flush();

            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            finalizarArquivos(saida, gravador, buffer_saida);
        }


        return resultado;
    }

    public synchronized void removerUrlDuplicada () {
        todasRequests = lerUrls();
        ArrayList<Request> semduplicadas = new ArrayList<>();

        todasRequests.forEach((request)->{
            if(!semduplicadas.contains(request)){
                semduplicadas.add(request);
            }
        });

        if (deletarArquivo(ARQUIVO))
            gravarUrl(semduplicadas);

    }

    public void buscarCodigo (String codigo) {

        FileInputStream entrada = null;
        InputStreamReader leitor = null;
        BufferedReader buffer_entrada = null;

        try {
            entrada = new FileInputStream(ARQUIVO_VIS);
            leitor = new InputStreamReader(entrada);
            buffer_entrada = new BufferedReader(leitor);
            String linha;
            linha = buffer_entrada.readLine();

            Request rq = null;

            while (linha != null) {
                if(!linha.equals("")) {
                    Request temp = new Request(linha.replace(SEPARATOR, ""));
                    if (temp.getCodigo().equals(codigo)) {
                        rq = temp;
                        break;
                    }
                }
                linha = buffer_entrada.readLine();
            }

            if (rq != null) {
                System.out.println("Origem: " + rq.getLink());
                if (!rq.getOrigem().equals("")) {
                    buscarCodigo(rq.getOrigem());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            finalizarArquivos(entrada, leitor, buffer_entrada);
        }

    }

    public Request buscarInformacoesCodigo (String codigo) {

        Request request = null;
        FileInputStream entrada = null;
        InputStreamReader leitor = null;
        BufferedReader buffer_entrada = null;

        try {
            entrada = new FileInputStream(ARQUIVO_VIS);
            leitor = new InputStreamReader(entrada);
            buffer_entrada = new BufferedReader(leitor);
            String linha; Request temp;

            while ((linha = buffer_entrada.readLine()) != null) {
                if(!linha.equals("")) {

                    temp = new Request(linha.replace(SEPARATOR, ""));

                    if (temp.getCodigo().equals(codigo)) {
                        request = temp;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            finalizarArquivos(entrada, leitor, buffer_entrada);
        }

        return request;
    }

    public synchronized ArrayList<Request> lerUrls () {
        return lerUrls(ARQUIVO);
    }

    public synchronized ArrayList<Request> lerUrls (String arquivo) {
        ArrayList<Request> requests = new ArrayList<>();

        File arq;
        FileInputStream entrada = null;
        InputStreamReader leitor = null;
        BufferedReader buffer_entrada = null;

        try {
            arq = new File(arquivo);
            entrada = new FileInputStream(arq);
            leitor = new InputStreamReader(entrada);
            buffer_entrada = new BufferedReader(leitor);
            String linha;

            while ((linha = buffer_entrada.readLine()) != null) {
                if(!linha.equals("")) {
                    Request rq = new Request(linha.replace(SEPARATOR, ""));
                    requests.add(rq);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            finalizarArquivos(entrada, leitor, buffer_entrada);
        }

        return requests;
    }

    public static void fecharManipuladoresEscrita() {
        try {
            if (buffer_saida != null) {
                buffer_saida.close();
            }

            if (saida != null) {
                saida.close();
            }

            if (gravador != null) {
                gravador.close();
            }

            if (buffer_entrada != null) {
                buffer_entrada.close();
            }

            if (leitor != null) {
                leitor.close();
            }

            if (entrada != null) {
                entrada.close();
            }

        } catch (IOException e) {
            System.out.println("ERRO ao fechar os manipuladores de escrita do arquivo");
        }
    }
}
