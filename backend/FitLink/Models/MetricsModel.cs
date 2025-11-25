namespace FitLink.Models
{
    public class MetricsModel
    {
        // setters públicos para o model binder conseguir preencher
        public string Height { get; set; } = string.Empty;
        public string Weight { get; set; } = string.Empty;

        public MetricsModel() { } // necessário pro Mongo

        public MetricsModel(string height, string weight)
        {
            Height = height;
            Weight = weight;
        }
    }
}
