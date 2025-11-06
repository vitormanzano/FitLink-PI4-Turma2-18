# FitLink-PI4-Turma2-18
Repositório para desenvolvimento do projeto FitLink. 

REQUISITOS FUNCIONAIS:
<br>
RF001-Cadastro de Aluno
<br>
RF002-Cadastro de Personal
<br>
RF003-Login de usuário
<br>
RF004-Buscar personal
<br>
RF005-Solicitar vínculo de serviço com personal
<br>
RF006-Consultar treino atua
<br>
RF007-Visualizar e editar perfil
<br>
RF008-Finalizar vínculo de serviço com personal/aluno
<br>
RF009-Home Personal / Dashboard alunos
<br>
RF010-Visualizar informações dos treinos
<br>
RF011-Visualizar perfil do aluno
<br>
RF012-Gerenciar treinos (CRUD)
<br>
RF013-Adicionar exercício
<br>
RF014-Remover exercício
<br>
RF015-Logout

REQUISITOS DESEJÁVEIS:
<br>
RF001-Recuperação de senha
<br>
RF002-Complemento de cadastro - Anamnese e avaliação física
<br>
RF003-Buscar personal - filtro de tags
<br>
RF004-Chat entre aluno e personal
<br>
RF005-Registrar treino concluído
<br>
RF006-Consultar histórico de treino
<br>
RF007-Visualizar informações dos treinos registrados
<br>
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

### Aluno 
Client significa aluno

| Método   | Endpoint                                              | Descrição                                   |
| ------   | ----------------------------------------------------- | --------------------------------------------|
| `POST `  | `/Client/register`                                    | Realiza login do aluno                      |
| `POST`   | `/Client/login`                                       | Cadastra um novo aluno                      |
| `GET`    | `/Client/getById/{id}`                                | Busca um aluno por ID                       |
| `GET`    | `/Client/getByCity/{city}`                            | Busca alunos por cidade                   | 
| `PATCH`  | `/Client/update/{id}`                                 | Atualiza os dados de um aluno               |
| `DELETE` | `/Client/delete/{id}`                                 | Deleta o registro de um aluno               |
| `PATCH`  | `/Client/linkToPersonal/{userId}/{personalTrainerId}` | Faz o vínculo entre um aluno e um personal  |                                         
|          |                                                       |                                             |

### Personal 

| Método   | Endpoint                                                | Descrição                                   |
| ------   | --------------------------------------------------------| --------------------------------------------|
| `POST `  | `/Personal/register`                                    | Realiza login do personal                   |
| `POST`   | `/Personal/login`                                       | Cadastra um novo personal                   |
| `GET`    | `/Personal/getById/{id}`                                | Busca um personal por ID                    |
| `GET`    | `/Personal/getByCity/{city}`                            | Busca personais por cidade                  | 
| `PATCH`  | `/Personal/update/{id}`                                 | Atualiza os dados de um personal            |
| `DELETE` | `/Personal/delete/{id}`                                 | Deleta o registro de um personal            |
|          |                                                         |                                             |


Intruções de Execução:

1-Clonar o repositório
git clone https://github.com/usuario/FitLink-PI4-Turma2-18.git
cd FitLink-PI4-Turma2-18

2-Executar o servidor Java
javac Servidor.java
java Servidor 3000

3-Certifique-se de que o MongoDB está em execução
mongod
