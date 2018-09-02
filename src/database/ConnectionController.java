package database;

import arquivo.ArquivoRequest;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import componentes.Request;
import core.RequestManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class ConnectionController {

    private static final String dbURL = "jdbc:mysql://162.221.187.66:3306/brianpl1_rastreador";
    private static final String username = "brianpl1";
    private static final String password = "7Kj853bpLj";
    private static Connection conn;
    private static ArrayList<Request> urls = new ArrayList<>();

    public ConnectionController() {
        conectar();
    }

    public void conectar() {
        try {
            conn = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Conexão concluída com sucesso!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insertURL(Request request) {
        try {
            String sql = "INSERT INTO url (URL, ID_ORIGEM) VALUES (?, ?)";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, request.getLink());
            statement.setString(2, request.getOrigem());
            statement.executeUpdate();

        } catch (MySQLIntegrityConstraintViolationException ex) {
            String log = "Registro duplicado: " + request.getLink() + ArquivoRequest.SEPARATOR;
            RequestManagement.gravarLog(log);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void addURL(Request request) {
        if (!urls.contains(request))
            urls.add(request);

        if (urls.size() >= 1000 || !RequestManagement.isRodar()) {
            salvarURLS();
        }
    }

    public void salvarURLS() {
        int tamanho = urls.size();
//        System.out.println("Salvando URLS! " + urls.size());
        for (int i = 0; i < tamanho; i++) {
            insertURL(urls.remove(i));
        }
    }

    public void addVisita(Request request) {
        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO visitas (ID_URL) VALUES (?)");
            statement.setString(1, request.getCodigo());
            statement.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("Erro ao adicionar registro como visitado.");
        }
    }

    public ArrayList<Request> getURLS() {
        ArrayList<Request> requests = new ArrayList<>();
        try {
            String sql = "SELECT ID, URL, ID_ORIGEM FROM url WHERE ID NOT IN (SELECT ID_URL FROM visitas) ORDER BY rand() LIMIT 100";

            Statement statement = conn.createStatement();
            ResultSet result;
            result = statement.executeQuery(sql);

            while (result.next()){
                String url = result.getString("URL");
                Long id = result.getLong("ID");
                Long idOrigem = result.getLong("ID_ORIGEM");
                requests.add(new Request(id, url, idOrigem));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (requests.size() == 0 && urls.size() > 0) {
            System.out.println("Oh! Oh! Não recuperei nenhum dado do servidor.");
            salvarURLS();
            requests = getURLS();
        }
        return requests;
    }



    public ObservableList<Request> getArvore(PreparedStatement statement) {

        ObservableList<Request> requests = FXCollections.observableArrayList();

        try {
            ResultSet result;
            result = statement.executeQuery();

            while (result.next()){
                String codigo = String.valueOf(result.getLong("ID"));
                String url = result.getString("URL");
                Integer filhos = Integer.valueOf(result.getString("FILHOS"));
                String visitado = result.getString("VISITA");
                Request rq = new Request();
                rq.setLink(url);
                rq.setFilhos(filhos);
                rq.setCodigo(codigo);
                rq.setVisitado(visitado);
                requests.add(rq);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return requests;
    }

    public ObservableList<Request> getArvore() {
        ObservableList<Request> requests = null;
        try {
            String sql = "SELECT u.ID, u.URL, COUNT(x.ID_ORIGEM) AS FILHOS, (CASE WHEN (COUNT(v.ID) > 0) THEN 'S' ELSE 'N' END) AS VISITA FROM url u LEFT JOIN url x ON x.ID_ORIGEM = u.ID LEFT JOIN visitas v ON v.ID_URL = u.ID WHERE u.ID_ORIGEM IS NULL GROUP BY u.ID ORDER BY u.ID";
            PreparedStatement statement = conn.prepareStatement(sql);
            requests = getArvore(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return requests;
    }

    public boolean uniqueUrl(String url) {
        boolean resultado = false;
        try {
            String sql = "SELECT count(u.URL) as QTDE FROM url u WHERE u.URL = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, url);
            ResultSet result;
            result = statement.executeQuery();
            result.next();
            int valor = result.getInt("QTDE");

            if(valor > 0) {
                resultado = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultado;
    }

    public ObservableList<Request> getCodigo(String codigo) {
        ObservableList<Request> requests = null;
        try {
            String sql = "SELECT u.ID, u.URL, COUNT(x.ID_ORIGEM) AS FILHOS, (CASE WHEN (COUNT(v.ID) > 0) THEN 'S' ELSE 'N' END) AS VISITA FROM url u LEFT JOIN url x ON x.ID_ORIGEM = u.ID LEFT JOIN visitas v ON v.ID_URL = u.ID WHERE u.ID_ORIGEM = ? GROUP BY u.ID ORDER BY u.ID";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, codigo);
            requests = getArvore(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return requests;
    }

}
