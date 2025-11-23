import validacoes.EmailValidator;
import validacoes.SignUpClientValidator;
import validacoes.SignUpPersonalValidator;
import validacoes.ValidationResult;

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

            // =====================================================
            // PROTOCOLO TEXTO: SIGNUP_CLIENT
            // Formato: SIGNUP_CLIENT:nome;email;senha;telefone;cidade
            // =====================================================
            if (primeiraLinha != null && primeiraLinha.startsWith("SIGNUP_CLIENT:")) {
                PrintWriter escritor = new PrintWriter(this.conexao.getOutputStream(), true);

                String dados = primeiraLinha.substring("SIGNUP_CLIENT:".length()).trim();
                String[] partes = dados.split(";", -1);

                if (partes.length < 5) {
                    escritor.println("ERRO:Dados insuficientes para cadastro de cliente");
                    this.conexao.close();
                    return;
                }

                String name     = partes[0];
                String email    = partes[1];
                String password = partes[2];
                String phone    = partes[3];
                String city     = partes[4];

                ValidationResult result = SignUpClientValidator.validate(
                    name,
                    email,
                    password,
                    phone,
                    city
                );

                if (result.isValid()) {
                    escritor.println("OK");
                } else {
                    escritor.println("ERRO:" + result.getMessage());
                }

                System.out.println(
                    "Cliente Android SIGNUP → " + email + " → " +
                    (result.isValid() ? "OK" : "ERRO: " + result.getMessage())
                );

                this.conexao.close();
                return;
            }

            // =====================================================
            // PROTOCOLO TEXTO: SIGNUP_PERSONAL
            // Formato: SIGNUP_PERSONAL:nome;email;senha;telefone;cidade;cpf;cref
            // =====================================================
            if (primeiraLinha != null && primeiraLinha.startsWith("SIGNUP_PERSONAL:")) {
                PrintWriter escritor = new PrintWriter(this.conexao.getOutputStream(), true);

                String dados = primeiraLinha.substring("SIGNUP_PERSONAL:".length()).trim();
                String[] partes = dados.split(";", -1);

                if (partes.length < 7) {
                    escritor.println("ERRO:Dados insuficientes para cadastro de personal");
                    this.conexao.close();
                    return;
                }

                String name     = partes[0];
                String email    = partes[1];
                String password = partes[2];
                String phone    = partes[3];
                String city     = partes[4];
                String cpf      = partes[5];
                String cref     = partes[6];

                ValidationResult result = SignUpPersonalValidator.validate(
                    name,
                    email,
                    password,
                    phone,
                    city,
                    cpf,
                    cref
                );

                if (result.isValid()) {
                    escritor.println("OK");
                } else {
                    escritor.println("ERRO:" + result.getMessage());
                }

                System.out.println(
                    "Personal Android SIGNUP → " + email + " → " +
                    (result.isValid() ? "OK" : "ERRO: " + result.getMessage())
                );

                this.conexao.close();
                return;
            }

            // =====================================================
            // PROTOCOLO TEXTO ANTIGO: VALIDAR_EMAIL (opcional manter)
            // =====================================================
            if (primeiraLinha != null && primeiraLinha.startsWith("VALIDAR_EMAIL:")) {
                PrintWriter escritor = new PrintWriter(this.conexao.getOutputStream(), true);

                String email = primeiraLinha.substring("VALIDAR_EMAIL:".length()).trim();
                ValidationResult valido = EmailValidator.validate(email);
                escritor.println(valido.isValid() ? "OK" : "ERRO");

                System.out.println("Cliente Android → " + email + " → " + (valido.isValid() ? "OK" : "ERRO"));
                this.conexao.close();
                return;
            }

            // Se não entrou em nenhum protocolo texto, volta o stream
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
                        // =================================================
                        // PROTOCOLO OBJETO: SignUpClient
                        // valor: "nome;email;senha;telefone;cidade"
                        // =================================================
                        case "SignUpClient": {
                            String[] partes = pedido.getValor().split(";", -1);
                            if (partes.length < 5) {
                                this.usuario.receba(new Resultado(false));
                                break;
                            }

                            String name     = partes[0];
                            String email    = partes[1];
                            String password = partes[2];
                            String phone    = partes[3];
                            String city     = partes[4];

                            ValidationResult result = SignUpClientValidator.validate(
                                name,
                                email,
                                password,
                                phone,
                                city
                            );

                            this.usuario.receba(new Resultado(result.isValid()));
                            break;
                        }

                        // =================================================
                        // PROTOCOLO OBJETO: SignUpPersonal
                        // valor: "nome;email;senha;telefone;cidade;cpf;cref"
                        // (se um dia você quiser usar via objeto)
                        // =================================================
                        case "SignUpPersonal": {
                            String[] partes = pedido.getValor().split(";", -1);
                            if (partes.length < 7) {
                                this.usuario.receba(new Resultado(false));
                                break;
                            }

                            String name     = partes[0];
                            String email    = partes[1];
                            String password = partes[2];
                            String phone    = partes[3];
                            String city     = partes[4];
                            String cpf      = partes[5];
                            String cref     = partes[6];

                            ValidationResult result = SignUpPersonalValidator.validate(
                                name,
                                email,
                                password,
                                phone,
                                city,
                                cpf,
                                cref
                            );

                            this.usuario.receba(new Resultado(result.isValid()));
                            break;
                        }

                        // Continua aceitando a operação antiga de validar só o email
                        case "ValidarEmail": {
                            ValidationResult result = EmailValidator.validate(pedido.getValor());
                            this.usuario.receba(new Resultado(result.isValid()));
                            break;
                        }

                        default:
                            // operação desconhecida – pode ignorar ou logar
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
