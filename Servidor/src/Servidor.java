import java.util.ArrayList;
import validacoes.EmailValidator;

public class Servidor {
    public static String PORTA_PADRAO = "3000";

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Uso esperado: java Servidor [PORTA]\n");
            return;
        }

        String porta = Servidor.PORTA_PADRAO;

        if (args.length == 1) {
            porta = args[0];
        }

        ArrayList<Parceiro> usuarios = new ArrayList<>();

        AceitadoraDeConexao aceitadoraDeConexao = null;

        try {
            aceitadoraDeConexao = new AceitadoraDeConexao(porta, usuarios);
            aceitadoraDeConexao.start();
        }
        catch (Exception erro) {
            System.err.println("Escolha uma porta apropriada e liberada para uso!\n");
            return;
        }

        for (;;) {
            System.out.println("\nO servidor está ativo! Para desativá-lo, use o comando \"desativar\".");
            System.out.println("Comando adicional: \"email <endereco>\" para validar e-mails.");
            System.out.print("> ");

            String comando = null;
            try {
                comando = Teclado.getUmString();
            }
            catch (Exception erro) {
                System.err.println("Erro ao ler comando.");
                continue;
            }

            if (comando == null) continue;
            comando = comando.trim();

            if (comando.equalsIgnoreCase("desativar")) {
                synchronized(usuarios) {
                    ComunicadoDeDesligamento comunicadoDeDesligamento = new ComunicadoDeDesligamento("Servidor encerrado pelo operador.");

                    for (Parceiro usuario : usuarios) {
                        try {
                            usuario.receba(comunicadoDeDesligamento);
                            usuario.adeus();
                        }
                        catch (Exception erro) { }
                    }
                }

                System.out.println("O servidor foi desativado!\n");
                System.exit(0);
            }

            else if (comando.toLowerCase().startsWith("email ")) {
                String email = comando.substring(6).trim();
                if (email.isEmpty()) {
                    System.err.println("Uso: email <endereco>");
                    continue;
                }

                boolean valido = EmailValidator.validar(email);
                if (valido)
                    System.out.println("Email válido!");
                else
                    System.out.println("Email inválido!");
            }

            else {
                System.err.println("Comando inválido. Use: desativar | email <endereco>");
            }
        }
    }
}
