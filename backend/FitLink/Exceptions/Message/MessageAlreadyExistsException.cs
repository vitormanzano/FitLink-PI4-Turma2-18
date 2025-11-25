namespace FitLink.Exceptions.Message;

public class MessageAlreadyExistsException : Exception
{
    public MessageAlreadyExistsException() : base("Mensagem já existe!") { }
    public MessageAlreadyExistsException(string message) : base(message) { }
}