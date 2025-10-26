namespace FitLink.Exceptions.User
{
    public class InvalidCredentials : Exception
    {
        public InvalidCredentials() : base("Credenciais inválidas!") { }
    }
}
