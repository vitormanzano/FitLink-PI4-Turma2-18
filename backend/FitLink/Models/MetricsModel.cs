namespace FitLink.Models
{
    public class MetricsModel
    {
        public string Height { get; private set; }
        public string Weight { get; private set; }

        public MetricsModel() { } // Construtor vazio necessário para o MongoDB

        public MetricsModel(string height, string weight)
        {
            Height = height;
            Weight = weight;
        }
    }
}
