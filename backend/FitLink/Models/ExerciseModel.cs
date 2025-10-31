namespace FitLink.Models
{
    public class ExerciseModel
    {
        public string Name { get; private set; }
        public string Instructions { get; private set; }
        public List<SetModel> Sets { get; private set; }

        public ExerciseModel() { } // Construtor vazio necessário para o MongoDB

        public ExerciseModel(string name, string instructions, List<SetModel> sets)
        {
            Name = name;
            Instructions = instructions;
            Sets = sets;
        }
    }
}
