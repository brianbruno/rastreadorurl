package componentes;

public class Request {

    private String codigo;
    private String link;
    private String origem;
    private String visitado;
    private String nome;
    private Integer filhos;

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

    public Integer getFilhos() {
        return filhos;
    }

    public void setFilhos(Integer filhos) {
        this.filhos = filhos;
    }
}
