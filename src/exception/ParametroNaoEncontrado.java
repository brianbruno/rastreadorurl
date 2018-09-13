package exception;

public class ParametroNaoEncontrado extends Exception {

    public ParametroNaoEncontrado(String nome) {
        super("Parâmetro: " + nome + " não encontrado");
    }
}
