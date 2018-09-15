package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.PropertyConfigurator;
import util.ScreenService;

import java.util.Properties;

import static arquivo.ConnectionParameters.getParameter;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {

            setLogProperties();

            Parent root = FXMLLoader.load(getClass().getResource("/templates/splash/splash.fxml"));
//            Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
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

    public static void setLogProperties() {
        String logpath = getParameter("LOGPATH");
        Properties log4jProperties = new Properties();
        log4jProperties.setProperty("log4j.rootLogger", "DEBUG, file");
        log4jProperties.setProperty("log4j.appender.file", "org.apache.log4j.RollingFileAppender");
        log4jProperties.setProperty("log4j.appender.file.File", logpath);
        log4jProperties.setProperty("log4j.appender.file.MaxFileSize", "5MB");
        log4jProperties.setProperty("log4j.appender.file.MaxBackupIndex", "10");
        log4jProperties.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
        log4jProperties.setProperty("log4j.appender.file.layout.ConversionPattern", "%d{dd-MM-yyyy HH:mm:ss} %-4p - %m%n");
        PropertyConfigurator.configure(log4jProperties);

//        System.setProperty("file.name", logpath);
    }
}
