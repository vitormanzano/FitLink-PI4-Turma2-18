public class ComunicadoDeDesligamento extends Comunicado {
    private final String motivo;

    public ComunicadoDeDesligamento() {
        this.motivo = "";
    }

    public ComunicadoDeDesligamento(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() { return motivo; }

    @Override
    public String toString() {
        return "Servidor será desligado. " + motivo;
    }
}