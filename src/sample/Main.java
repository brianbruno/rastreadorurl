package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.ScreenService;

import static arquivo.ConnectionParameters.getParameter;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            String logpath = getParameter("LOGPATH");
            System.setProperty("file.name", logpath);

            Parent root = FXMLLoader.load(getClass().getResource("/templates/splash/splash.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
            primaryStage.setTitle("Rastreador de URL");
            primaryStage.setScene(new Scene(root));
            primaryStage.initStyle(StageStyle.UNDECORATED);

            primaryStage.show();
        } catch (Exception e) {
            ScreenService.showStackTrace(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
