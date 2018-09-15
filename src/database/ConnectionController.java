package database;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import componentes.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.LogService;
import util.ScreenService;

import java.sql.*;
import java.util.ArrayList;

public class ConnectionController {

    private String dbURL;
    private String username;
    private String password;

    private static Connection conn;

    public ConnectionController(String dburl, String username, String password) {
        this.dbURL = dburl;
        this.username = username;
        this.password = password;
    }

    public boolean conectar() {
        boolean resultado = false;
        try {
            conn = DriverManager.getConnection(dbURL, username, password);
            LogService.addLogInfo("Conexão concluída com sucesso!");
            resultado = true;
        } catch (SQLException ex) {
            ScreenService.showStackTrace(ex);
        }
        return resultado;
    }

    public void desconectar() {
        try {
            conn.close();
        } catch (SQLException ex) {
            ScreenService.showStackTrace(ex);
        }
    }

    public synchronized void insertURL(Request request) {
        try {
            beginTrasaction();

            String sql = "INSERT INTO url (URL, ID_ORIGEM) VALUES (?, ?)";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setLong(1, getIdUrl(request.getLink()));
            statement.setString(2, request.getOrigem());
            statement.executeUpdate();

            commit();
        } catch (MySQLIntegrityConstraintViolationException ex) {
            LogService.addLogWarn("Registro duplicado: " + request.getLink());
        } catch (SQLException ex) {
            rollback();
            ScreenService.showStackTrace(ex);
        }
    }

    public void addURL(Request request) {
        insertURL(request);
    }

    public void addVisita(Request request) {
        try {
            beginTrasaction();

            PreparedStatement statement = conn.prepareStatement("INSERT INTO visitas (ID_URL) VALUES (?)");
            statement.setString(1, request.getCodigo());
            statement.executeUpdate();

            commit();
        } catch (SQLException ex) {
            rollback();
            ScreenService.showStackTrace(ex);
        }
    }

    private long getIdUrl(String url) {
        long idUrl = 0;

        try {
            String sql = "SELECT l.ID FROM links l WHERE l.URL = ? LIMIT 1";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, url);
            ResultSet result;
            result = statement.executeQuery();

            if (result.next()) {
                idUrl = result.getLong("ID");
            } else {
                beginTrasaction();
                statement = conn.prepareStatement("INSERT INTO links (URL) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, url);
                statement.executeUpdate();
                commit();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idUrl = generatedKeys.getLong(1);
                    }
                }
            }

        } catch (SQLException ex) {
            rollback();
            ScreenService.showStackTrace(ex);
        }

        return idUrl;
    }

    public ArrayList<Request> getURLS() {
        ArrayList<Request> requests = new ArrayList<>();
        try {
            String sql = "SELECT ID, URL, ID_ORIGEM FROM url_pendente";

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
            LogService.addLogError(ex.getMessage());
            ex.printStackTrace();
        }

        if (requests.size() == 0) {
            LogService.addLogError("Oh! Oh! Não recuperei nenhum dado do servidor.");
        }
        return requests;
    }

    private ObservableList<Request> getArvore(PreparedStatement statement) {

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
            String sql = "SELECT u.ID, l.URL, COUNT(x.ID_ORIGEM) AS FILHOS, (CASE WHEN (COUNT(v.ID) > 0) THEN 'S' ELSE 'N' END) AS VISITA FROM url u JOIN links l ON u.URL =  l.ID LEFT JOIN url x ON x.ID_ORIGEM = u.ID LEFT JOIN visitas v ON v.ID_URL = u.ID WHERE u.ID_ORIGEM IS NULL GROUP BY u.ID ORDER BY u.ID";
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

            if(valor == 0) {
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
            String sql = "SELECT u.ID, l.URL, COUNT(x.ID_ORIGEM) AS FILHOS, (CASE WHEN (COUNT(v.ID) > 0) THEN 'S' ELSE 'N' END) AS VISITA FROM url u JOIN links l ON u.URL =  l.ID LEFT JOIN url x ON x.ID_ORIGEM = u.ID LEFT JOIN visitas v ON v.ID_URL = u.ID WHERE u.ID_ORIGEM = ? GROUP BY u.ID ORDER BY u.ID";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, codigo);
            requests = getArvore(statement);
        } catch (Exception ex) {
            LogService.addLogError(ex.getMessage());
            ex.printStackTrace();
        }
        return requests;
    }

    private void beginTrasaction() {
        try {
            conn.setAutoCommit(false);
        } catch (Exception e) {
            ScreenService.showStackTrace(e);
        }
    }

    private void commit() {
        try {
            conn.commit();
        } catch (Exception e) {
            ScreenService.showStackTrace(e);
        }
    }

    private void rollback() {
        try {
        conn.rollback();
        } catch (Exception e) {
            ScreenService.showStackTrace(e);
        }
    }
}
