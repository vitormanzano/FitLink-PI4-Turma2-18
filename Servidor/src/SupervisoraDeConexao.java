import validacoes.ValidadorEmail;
import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread {
    private Parceiro usuario;
    private Socket conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios) throws Exception {
        if (conexao == null)
            throw new Exception("Conexão ausente");
        if (usuarios == null)
            throw new Exception("Usuários ausentes");

        this.conexao = conexao;
        this.usuarios = usuarios;
    }

    @Override
    public void run() {
        try {
            this.conexao.setSoTimeout(200); // pequeno timeout pra leitura inicial
            InputStream rawIn = this.conexao.getInputStream();
            rawIn.mark(512); // marca posição no stream

            BufferedReader leitor = new BufferedReader(new InputStreamReader(rawIn));
            String primeiraLinha = null;

            try {
                primeiraLinha = leitor.readLine();
            } catch (IOException e) {
                // ignore timeout / erro
            }

            this.conexao.setSoTimeout(0); // desativa timeout

            if (primeiraLinha != null && primeiraLinha.startsWith("VALIDAR_EMAIL:")) {
                PrintWriter escritor = new PrintWriter(this.conexao.getOutputStream(), true);

                String email = primeiraLinha.substring("VALIDAR_EMAIL:".length()).trim();
                boolean valido = ValidadorEmail.validar(email);
                escritor.println(valido ? "OK" : "ERRO");

                System.out.println("Cliente Android → " + email + " → " + (valido ? "OK" : "ERRO"));
                this.conexao.close();
                return;
            }

            rawIn.reset(); // volta pro início do stream pra ler como objeto
            ObjectOutputStream transmissor = new ObjectOutputStream(this.conexao.getOutputStream());
            ObjectInputStream receptor = new ObjectInputStream(rawIn);

            this.usuario = new Parceiro(this.conexao, receptor, transmissor);

            synchronized (this.usuarios) {
                this.usuarios.add(this.usuario);
            }

            for (;;) {
                Comunicado comunicado = this.usuario.envie();
                if (comunicado == null) break;

                if (comunicado instanceof PedidoDeOperacao) {
                    PedidoDeOperacao pedido = (PedidoDeOperacao) comunicado;
                    switch (pedido.getOperacao()) {
                        case "ValidarEmail":
                            boolean isValid = ValidadorEmail.validar(pedido.getValor());
                            this.usuario.receba(new Resultado(isValid));
                            break;
                        default:
                            break;
                    }
                } else if (comunicado instanceof PedidoParaSair) {
                    synchronized (this.usuarios) {
                        this.usuarios.remove(this.usuario);
                    }
                    this.usuario.adeus();
                    break;
                }
            }

        } catch (Exception erro) {
            try {
                this.conexao.close();
            } catch (Exception ignore) {}
        }
    }
}
