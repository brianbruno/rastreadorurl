package componentes;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.json.simple.JSONObject;

public class Ancestral {

    private String codigo;
    private final SimpleStringProperty site;
    private final SimpleIntegerProperty filhos;

    public Ancestral(String site, Integer filhos) {
        this.site = new SimpleStringProperty(site);
        this.filhos = new SimpleIntegerProperty(filhos);
    }

    public Ancestral (int filhos, String codigo) {
        this.codigo = codigo;
        this.site = null;
        this.filhos = new SimpleIntegerProperty(filhos);
    }

    public String getSite() {
        return site.get();
    }

    public SimpleStringProperty siteProperty() {
        return site;
    }

    public void setSite(String site) {
        this.site.set(site);
    }

    public int getFilhos() {
        return filhos.get();
    }

    public SimpleIntegerProperty filhosProperty() {
        return filhos;
    }

    public void setFilhos(int filhos) {
        this.filhos.set(filhos);
    }

    public String toJson() {
        JSONObject novoDado = new JSONObject();
        novoDado.put("codigo", codigo);
        novoDado.put("filhos", String.valueOf(getFilhos()));
        return novoDado.toJSONString() ;
    }
}
