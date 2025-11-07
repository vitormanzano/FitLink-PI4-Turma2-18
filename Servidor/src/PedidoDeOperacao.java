public class PedidoDeOperacao extends Comunicado {
    private String operacao;
    private String valor;

    public PedidoDeOperacao(String operacao, String valor) {
        this.operacao = operacao;
        this.valor = valor;
    }

    public String getOperacao() { return this.operacao; }
    public String getValor() { return this.valor; }
    public String toString() { return ("" + this.operacao + this.valor); }
}
