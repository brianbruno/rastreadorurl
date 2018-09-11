package util;

import javax.swing.*;

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

}
