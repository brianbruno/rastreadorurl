package core;

import arquivo.ArquivoRequest;
import arquivo.ArquivoAncestrais;
import arquivo.ConnectionParameters;
import componentes.Ancestral;
import componentes.Request;
import database.ConnectionController;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class RequestManagement {

    private ArrayList<Request> links;
    private ArrayList<Request> saveLinks;
    private static boolean rodar = true;
    private boolean botOcioso = false;
    private static ArquivoManagement am = new ArquivoManagement();
    private ConnectionController conn;

    public RequestManagement() {
        conn = new ConnectionParameters().startConnection();
        this.saveLinks = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public RequestManagement(ConnectionController conn) {
        this.conn = conn;
        this.saveLinks = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public synchronized Request getUrl() {

        Request request = null;
        if (rodar) {
            if (links.size() == 0 || botOcioso) {
                this.links = conn.getURLS();
                if (botOcioso && links.size() == 0) {
                    System.out.println("PERIGO! Links esgotados.");
                }
                if (botOcioso)
                    setBotOcioso(false);
            }

            if (links.size() == 0) {
                request = null;
            } else {
                request = links.remove(0);
            }
        }

        return request;
    }

    public synchronized void setBotOcioso(boolean botOcioso) {
        this.botOcioso = botOcioso;
    }

    public static boolean isRodar() {
        return rodar;
    }

    public void setRodar(boolean rodar) {
        this.rodar = rodar;
    }


    public static synchronized void gravarLog(String log){
        am.gravarLog(log);
    }

    public void novoUrl (String url, String origem) {
        Request rq = new Request();
        rq.setLink(url);
        rq.setOrigem(origem);
        conn.addURL(rq);
    }

    public void salvarUrls() {
        conn.salvarURLS();
    }

    public void setVisitado(Request rq) {
        conn.addVisita(rq);
    }

    public void desconectar() {
        conn.desconectar();
    }

}
