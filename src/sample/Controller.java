package sample;

import arquivo.ConnectionParameters;
import componentes.Ancestral;
import componentes.Request;
import core.ArquivoManagement;
import core.Bot;
import core.RequestManagement;
import database.ConnectionController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import util.ScreenService;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static ArrayList<Bot> bots;
    private static List<String> listaCodigos;
    private static final int THREADS = 2;
    private RequestManagement rm;
    private ConnectionController conn;
    @FXML
    private TableView tableFilhos;
    @FXML
    private TableColumn<Request, String> colSite;
    @FXML
    private TableColumn<Request, Integer> colFilhos;
    public ObservableList<Request> listaAncestrais;
    @FXML
    public Button closeButton;
    @FXML
    public Button backButton;
    @FXML
    public Label labelStatus;
    String url_add = null;
    public static boolean LOG_ATIVO = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ConnectionParameters connectionParameters = new ConnectionParameters();
        conn = connectionParameters.startConnection();

        if (conn == null) {
            ScreenService.showErrorMessage("Dados de conexão incorretos");
            System.exit(0);
        }

        rm = new RequestManagement();

        listaCodigos = new ArrayList<>();
        colSite.setCellValueFactory(
                new PropertyValueFactory<>("link"));
        colFilhos.setCellValueFactory(
                new PropertyValueFactory<>("filhos"));
        colFilhos.setSortType(TableColumn.SortType.ASCENDING);
        atualizarAncestrais();

        tableFilhos.setRowFactory(row -> new TableRow<Request>(){
            @Override
            public void updateItem(Request item, boolean empty){
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                    for(int i=0; i<getChildren().size();i++){
                        ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                        ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: white");
                    }
                } else {
                    //Now 'item' has all the info of the Person in this row
                    if (item.getVisitado().equals("N")) {
                        //We apply now the changes in all the cells of the row
                        for(int i=0; i<getChildren().size();i++){
                            ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                            ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: #fce4ec");
                        }
                    } else {
                        if(getTableView().getSelectionModel().getSelectedItems().contains(item)){
                            for(int i=0; i<getChildren().size();i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: white");
                            }
                        }
                        else{
                            for(int i=0; i<getChildren().size();i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: white");
                            }
                        }
                    }
                }
            }
        });
    }

    @FXML
    private void iniciarRastreamento(ActionEvent event) {
        bots = new ArrayList<>();
        rm.setRodar(true);
        labelStatus.setText("O bot está buscando...");
        for (int i = 0; i < THREADS; i++) {
            bots.add(new Bot(rm));
            bots.get(i).start();
        }
    }

    @FXML
    private void pararRastreamento(ActionEvent event) {
        rm.setRodar(false);
        labelStatus.setText("Parando o bot...");
        for (int i = 0; i < THREADS; i++) {
            try {
                bots.get(i).join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Bot.setBots(0);
        labelStatus.setText("O bot está parado!");
    }

    @FXML
    private void closeButtonAction(){
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    @FXML
    private void atualizarAncestrais(){
        if (listaCodigos.size() > 0) {
            listaAncestrais = conn.getCodigo(listaCodigos.get(listaCodigos.size()-1));
            tableFilhos.setItems(listaAncestrais);
        } else {
            listaAncestrais = conn.getArvore();
            tableFilhos.setItems(listaAncestrais);
        }
    }

    @FXML
    public void clickItem(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Request linha = (Request) tableFilhos.getSelectionModel().getSelectedItem();
            if (linha != null) {
                listaAncestrais = conn.getCodigo(linha.getCodigo());
                tableFilhos.setItems(listaAncestrais);
                backButton.setDisable(false);
                listaCodigos.add(linha.getCodigo());
            }
        }
    }

    @FXML
    public void clickButtonBack(MouseEvent event) {
        listaCodigos.remove(listaCodigos.size()-1);
        if (listaCodigos.size() > 0) {
            listaAncestrais = conn.getCodigo(listaCodigos.get(listaCodigos.size()-1));
            tableFilhos.setItems(listaAncestrais);
        } else {
            atualizarAncestrais();
            backButton.setDisable(true);
        }

    }

    @FXML
    public void addURL() {

        TextInputDialog dialogoNome = new TextInputDialog();

        dialogoNome.setTitle("Cadastrar URL");
        dialogoNome.setHeaderText("Cadastrar nova URL para pesquisa");
        dialogoNome.setContentText("Digite a URL:");
        // se o usuário fornecer um valor, assignamos ao nome
        dialogoNome.showAndWait().ifPresent(v -> url_add = v);

        if (url_add != null && !url_add.equals("")) {
            Request rq = new Request();
            rq.setLink(url_add);
            rq.setOrigem(null);
            conn.insertURL(rq);
            atualizarAncestrais();
        }
    }
}
