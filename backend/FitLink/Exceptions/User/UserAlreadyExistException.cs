namespace FitLink.Exceptions.User
{
    public class UserAlreadyExistException : Exception
    {
        public UserAlreadyExistException() : base("Usuário já existe!") { }
        
    }
}
