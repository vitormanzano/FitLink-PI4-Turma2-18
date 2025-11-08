namespace FitLink.Models
{
    public class SetModel
    {
        public int Number { get; private set; }
        public int NumberOfRepetitions { get; private set; }
        public double Weight { get; private set; }

        public SetModel() { } // Construtor vazio necessário para o MongoDB

        public SetModel(int number, int numberOfRepetitions, double weight)
        {
            Number = number;
            NumberOfRepetitions = numberOfRepetitions;
            Weight = weight;
        }
    }
}
