package core;

import arquivo.ArquivoRequest;
import componentes.Request;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Bot extends Thread {

    private String content;
    private RequestManagement rm;
    private static int bots = 1;
    private int bot_id;
    private Request url;
    private List<String> palavrasBloqueadas = Arrays.asList("buaa.edu.cn", "apache.org", "Banana");

    public Bot(RequestManagement rm) {
        bot_id = bots++;
        System.out.println("Iniciando bot " + bot_id);
        this.rm = rm;
    }

    public void run() {
        navegar();
    }

    public void navegar() {

        while (rm.isRodar()) {
            if ((url = rm.getUrl()) != null) {
                ArrayList<Integer> indexs;
                try {
                    URLConnection connection = null;
                    String path = url.getLink() + "/";
                    rm.setVisitado(url);
                    connection = new URL(path).openConnection();
                    Scanner scanner = new Scanner(connection.getInputStream());
                    scanner.useDelimiter("\\Z");
                    content = scanner.next();
                    indexs = verificarConteudo();
                    adicionarUrls(indexs);
                } catch (Exception ex) {
                    String log = "Erro no bot " + bot_id + "! " + ex.getMessage() + ArquivoRequest.SEPARATOR;
                    log += "Link: " + url.getLink() + ArquivoRequest.SEPARATOR;
                    RequestManagement.gravarLog(log);
                }
            } else {
                try {
                    System.out.println("Bot " + bot_id + " ocioso.");
                    Thread.sleep(30000);
                    rm.setBotOcioso(true);
                } catch (Exception err) {
                    RequestManagement.gravarLog("Erro no bot " + bot_id + "! " + err.getMessage());
                    RequestManagement.gravarLog("Erro ao colocar Thread para dormir!");
                }
            }
        }

        System.out.println("Bot " + bot_id + " finalizado.");
    }

    private ArrayList<Integer> verificarConteudo () {
        int i = 0;
        String busca = "http://";
        ArrayList<Integer> indexs = new ArrayList<>();

        int index = content.indexOf(busca);
        while (index >= 0) {
            indexs.add(index);
            index = content.indexOf(busca, index + 1);
        }

        busca = "https://";
        index = content.indexOf(busca);
        while (index >= 0) {
            indexs.add(index);
            index = content.indexOf(busca, index + 1);
        }

        return indexs;
    }

    private void adicionarUrls(ArrayList<Integer> indexs) {
        indexs.forEach((ind)-> {
            String url = recortarString(ind);
            boolean contemPalavrasBloqueadas = palavrasBloqueadas.stream().anyMatch(s -> url.contains(s));
            if(!contemPalavrasBloqueadas) {
                rm.novoUrl(url, this.url.getCodigo());
            }
        });
    }

    private String recortarString (Integer index) {
        int j;
        String urlFinal;
        String substring = content.substring(index, index+50);
        if (substring.substring(0, 8).equals("https://")) {
            j = 8;
        } else
            j = 7;

        char letra = substring.charAt(j);

        while (j < substring.length() && (letra != '\"' && letra != '/' && letra != '<' && letra != ' ' && letra != '\'')) {
            letra = substring.charAt(j);
            j++;
        }

        urlFinal = substring.substring(0, j-1);
        return urlFinal;
    }

    public static void setBots(int bots) {
        Bot.bots = bots;
    }
}
