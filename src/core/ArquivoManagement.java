package core;

import arquivo.ArquivoAncestrais;
import arquivo.ArquivoRequest;
import componentes.Ancestral;
import componentes.Request;
import javafx.collections.ObservableList;
import sample.Controller;

import java.util.ArrayList;

public class ArquivoManagement {

    private ArquivoRequest arqRequest;

    public ArquivoManagement() {
        arqRequest = new ArquivoRequest();
    }

    public boolean gravarLog (String log) {
        if (Controller.LOG_ATIVO)
            return arqRequest.gravarLog(log);
        else
            return false;
    }

}
