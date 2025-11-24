namespace FitLink.Exceptions.Message
{
    public class MessageNotFoundException : Exception
    {
        public MessageNotFoundException() : base("Mensagem não encontrada!") { }
        public MessageNotFoundException(string message) : base(message) { }
    }
}
