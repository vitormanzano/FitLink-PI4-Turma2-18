namespace FitLink.Exceptions.Train
{
    public class TrainNotFoundException : Exception
    {
        public TrainNotFoundException(string message = "Treino não encontrado!") : base(message)
        {
        }
    }
}
