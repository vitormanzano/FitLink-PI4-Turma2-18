# FitLink-PI4-Turma2-18
Repositório para desenvolvimento do projeto FitLink. 

REQUISITOS FUNCIONAIS:
RF001-Cadastro de Aluno
RF002-Cadastro de Personal
RF003-Login de usuário
RF004-Buscar personal
RF005-Solicitar vínculo de serviço com personal
RF006-Consultar treino atua
RF007-Visualizar e editar perfil
RF008-Finalizar vínculo de serviço com personal/aluno
RF009-Home Personal / Dashboard alunos
RF010-Visualizar informações dos treinos
RF011-Visualizar perfil do aluno
RF012-Gerenciar treinos (CRUD)
RF013-Adicionar exercício
RF014-Remover exercício
RF015-Logout

REQUISITOS DESEJÁVEIS:
RF001-Recuperação de senha
RF002-Complemento de cadastro - Anamnese e avaliação física
RF003-Buscar personal - filtro de tags
RF004-Chat entre aluno e personal
RF005-Registrar treino concluído
RF006-Consultar histórico de treino
RF007-Visualizar informações dos treinos registrados
RF008-Excluir conta

Servidor Java:

O servidor será desenvolvido utilizando a linguagem Java, sendo responsável por atuar como uma ferramenta central de validação de dados dentro do sistema. Ele terá como principal função garantir a integridade, consistência e segurança das informações trafegadas entre o cliente e o banco de dados.
Através da implementação de rotinas de verificação, o servidor irá validar formatos, tipos e regras de negócio antes que os dados sejam armazenados ou processados.

MongoDB:

No banco de dados MongoDB do projeto serão armazenadas três coleções principais: users, treino e personal.
A coleção users guarda as informações dos clientes, incluindo nome, e-mail, senha, telefone e, opcionalmente, um identificador do personal trainer (personalId).
A coleção personal contém os dados dos profissionais de educação física, como nome, e-mail, senha, telefone, CREF e CPF.
Já a coleção treino registra os treinos personalizados, com o nome do treino, o identificador do cliente (clienteId), o identificador do personal responsável (personalId) e uma lista de exercícios. Cada exercício possui nome, instruções e uma ou mais séries, que detalham o número da série, o número de repetições, a carga utilizada (em kg) e observações adicionais.

![img.png](DiagramaMongo.png)

Processo Escolhido:

O desenvolvimento seguiu uma metodologia ágil baseada em Scrum, com sprints semanais e reuniões .de acompanhamento. As tarefas foram organizadas em um board Git Project, e o controle de versão foi realizado via GitHub.

Arquitetura Resumida:

EndPoints:

| Método | Endpoint          | Descrição                               |
| ------ | ----------------- | --------------------------------------- |
| `POST` | `/login`          | Realiza login do usuário                |
| `POST` | `/cadastro/aluno` | Cadastra um novo aluno                  |
| `GET`  | `/personais`      | Retorna lista de personais cadastrados  |
| `POST` | `/vinculo`        | Solicita vínculo entre aluno e personal |
| `GET`  | `/treino/:id`     | Retorna o treino atual do aluno         |
|        |                   |                                         |
|        |                   |                                         |
|        |                   |                                         |


Intruções de Execução:

1-Clonar o repositório
git clone https://github.com/usuario/FitLink-PI4-Turma2-18.git
cd FitLink-PI4-Turma2-18

2-Executar o servidor Java
javac Servidor.java
java Servidor 3000

3-Certifique-se de que o MongoDB está em execução
mongod