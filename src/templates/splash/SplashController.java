package templates.splash;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML
    private StackPane rootPane;
    @FXML
    private Label lblFooter;

    private double xOffset = 0;
    private double yOffset = 0;

    public void initialize(URL url, ResourceBundle rb) {
        lblFooter.setText("Carregando sistema...");
        new SplashScreen().start();
    }

    class SplashScreen extends Thread {
        @Override
        public void run () {
            try {
                Thread.sleep(1000);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        Parent root = null;
                        try {
                            lblFooter.setText("Conectando ao banco de dados...");
                            root = FXMLLoader.load(getClass().getResource("/sample/sample.fxml"));
                        } catch (Exception ex) {
                            System.err.println(ex.getMessage());
                        }

                        Stage stage = new Stage();

                        lblFooter.setText("Ajustando os detalhes...");
                        stage.setTitle("Rastreador de URL");
                        stage.setScene(new Scene(root));
                        stage.initStyle(StageStyle.UNDECORATED);

                        root.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                xOffset = event.getSceneX();
                                yOffset = event.getSceneY();
                            }
                        });
                        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                stage.setX(event.getScreenX() - xOffset);
                                stage.setY(event.getScreenY() - yOffset);
                            }
                        });

                        stage.show();
                        rootPane.getScene().getWindow().hide();
                    }
                });

            } catch (Exception e) {
                System.err.println("Erro.");
            }
        }
    }
}
