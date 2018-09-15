package templates.splash;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.LogService;
import util.ScreenService;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML
    private StackPane rootPane;
    @FXML
    private Label lblFooter;
    @FXML
    ProgressBar barra;

    private double xOffset = 0;
    private double yOffset = 0;

    public void initialize(URL url, ResourceBundle rb) {
        lblFooter.setText("Carregando sistema...");
//        start();
        new SplashScreen().start();
    }

    private void start() {
        Service<Void> servico = new Service<Void>() {
            @Override
            protected Task <Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            updateMessage("Carregando...");
                            updateProgress(0, 10);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    updateProgress(2, 10);
                                    Parent root = null;
                                    try {
                                        LogService.addLogInfo("Tentando conexão com o banco de dados");
                                        updateProgress(3, 10);
                                        updateMessage("Conectando ao banco de dados...");
                                        root = FXMLLoader.load(getClass().getResource("/sample/sample.fxml"));
                                        updateProgress(7, 10);
                                        updateMessage("Conexão realizada!");
                                    } catch (Exception ex) {
                                        LogService.addLogError(ex.getMessage());
                                        ScreenService.showStackTrace(ex);
                                    }

                                    Stage stage = new Stage();
                                    updateProgress(9, 10);
                                    LogService.addLogInfo("Ajustando detalhes de tela");
                                    updateMessage("Ajustando os detalhes...");
                                    updateProgress(10, 10);
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

                                    LogService.addLogInfo("Tela carregada com sucesso.");
                                }
                            });

                        } catch (Exception e) {
                            LogService.addLogError(e.getMessage());
                            ScreenService.showStackTrace(e);
                        }

                        return null;
                    }
                };
            }
        };

        lblFooter.textProperty().bind(servico.messageProperty());
        barra.progressProperty().bind(servico.progressProperty());
        //precisa inicializar o Service
        servico.restart();
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
                            LogService.addLogInfo("Tentando conexão com o banco de dados");
                            lblFooter.setText("Conectando ao banco de dados...");
                            root = FXMLLoader.load(getClass().getResource("/sample/sample.fxml"));
                            LogService.addLogInfo("Conexão realizada!");
                        } catch (Exception ex) {
                            LogService.addLogError(ex.getMessage());
                            ScreenService.showStackTrace(ex);
                        }

                        Stage stage = new Stage();

                        LogService.addLogInfo("Ajustando detalhes de tela");
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

                        LogService.addLogInfo("Tela carregada com sucesso.");
                    }
                });

            } catch (Exception e) {
                LogService.addLogError(e.getMessage());
                ScreenService.showStackTrace(e);
            }
        }
    }
}
