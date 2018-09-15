package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ScreenService {

    public static void showErrorMessage(String mensagem) {

        // create a jframe
        JFrame frame = new JFrame("Erro");

        // show a joptionpane dialog using showMessageDialog
        JOptionPane.showMessageDialog(frame,
                mensagem,
                "Erro fatal",
                JOptionPane.ERROR_MESSAGE);

        System.exit(0);
    }

    public static void showStackTrace(Exception ex) {

        LogService.addLogFatal(ex.getMessage());

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro fatal");
        alert.setHeaderText("Ops! Um erro inesperado ocorreu.");
        alert.setContentText(ex.getMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Stacktrace:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();

        System.exit(0);

    }

}
