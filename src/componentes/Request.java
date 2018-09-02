package componentes;

import arquivo.ArquivoAncestrais;
import arquivo.ArquivoUtil;
import core.RequestManagement;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Random;

public class Request {

    private String codigo;
    private String link;
    private String origem;
    private String visitado;
    private String nome;
    private Integer filhos;

    public Request (String linha) {
        montarObjeto(linha);
    }

    public Request(Long codigo, String link, Long origem) {
        this.codigo = String.valueOf(codigo);
        this.link = link;
        this.origem = String.valueOf(origem);
    }

    public Request() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getVisitado() {
        return visitado;
    }

    public void setVisitado(String visitado) {
        this.visitado = visitado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        boolean resultado = false;
        if (o instanceof Request && ((Request) o).getLink().equals(this.getLink())) {
            resultado = true;
        }
        return resultado;
    }

    private void montarObjeto (String linha) {
        Request request = null;

        try {
            if(linha != null && !linha.equals("")) {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(linha);
                JSONObject jsonObject = (JSONObject) obj;
                String cod = (String) jsonObject.get("codigo");
                String nome = (String) jsonObject.get("nome");
                String link = (String) jsonObject.get("link");
                String visitado = (String) jsonObject.get("visitado");
                String origem = (String) jsonObject.get("origem");

                setCodigo(cod);
                setNome(nome);
                setLink(link);
                setOrigem(origem);
                setVisitado(visitado);
            }
        } catch (Exception e) {
            System.err.println("Erro ao montar objeto.");
            e.printStackTrace();
        }
    }

    public String toJson () {
        JSONObject novoDado = new JSONObject();
        String novaString;

        if (getCodigo() == null) {
            System.err.println("PERIGO! Código: null Link: " + getLink());
        }

        novoDado.put("codigo", getCodigo());
        novoDado.put("nome", getNome());
        novoDado.put("link", getLink());
        novoDado.put("visitado", getVisitado());
        novoDado.put("origem", getOrigem());

        novaString = novoDado.toJSONString() + ArquivoUtil.SEPARATOR;

        return novaString;
    }

    public Integer getFilhos() {
        return filhos;
    }

    public void setFilhos(Integer filhos) {
        this.filhos = filhos;
    }
}
