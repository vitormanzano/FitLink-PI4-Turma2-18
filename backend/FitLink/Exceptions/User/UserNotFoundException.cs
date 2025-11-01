namespace FitLink.Exceptions.User
{
    public class UserNotFoundException : Exception
    {
        public UserNotFoundException() : base("Usuário não encontrado!") { }
    }
}
