namespace FitLink.Exceptions.User
{
    public class UserAlreadyExist : Exception
    {
        public UserAlreadyExist() : base("Usuário já existe!") { }
        
    }
}
