namespace FitLink.Exceptions.User
{
    public class UserNotFound : Exception
    {
        public UserNotFound() : base("Usuário não encontrado!") { }
    }
}
