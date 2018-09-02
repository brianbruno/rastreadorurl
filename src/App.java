import componentes.Request;
import core.Bot;
import core.RequestManagement;
import database.ConnectionController;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class App {

    private static ArrayList<Bot> bots;
    private static final int THREADS = 2;

    public static void main(String[] args) {
        System.out.println("Hello!");
        RequestManagement rm = new RequestManagement();
        bots = new ArrayList<>();
        int opcao = -1;

        while (opcao  != 0) {
            System.out.println("Digite o código da opção desejada: ");
            Scanner ler =  new Scanner(System.in);
            System.out.println("1 - Scanear");
//            System.out.println("2 - Verificar site");

            opcao = ler.nextInt();

            switch (opcao) {
                case 1:
                    ler.nextLine();
                    rm.setRodar(true);
                    for (int i = 0; i < THREADS; i++) {
                        bots.add(new Bot(rm));
                        bots.get(i).start();
                    }
                    System.out.println("Digite uma tecla para parar...");
                    ler.nextLine();
                    rm.setRodar(false);
                    for (int i = 0; i < THREADS; i++) {
                        try {
                            bots.get(i).join();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Bot.setBots(0);
                    bots = new ArrayList<>();
                    break;
                case  0:
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }

    }
}
