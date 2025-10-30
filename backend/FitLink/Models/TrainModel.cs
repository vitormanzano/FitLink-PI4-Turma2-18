using MongoDB.Bson.Serialization.Attributes;

namespace FitLink.Models
{
    public class TrainModel
    {
        [BsonId]
        [BsonRepresentation(MongoDB.Bson.BsonType.String)] // Salva o Guid como string
        public Guid Id { get; private set; }
        public string Name { get; private set; }
        public Guid UserId { get; private set; } // ID do usuário ao qual o treino pertence
        public Guid PersonalId { get; private set; } // ID do personal trainer associado ao treino

        public List<ExerciseModel> Exercises { get; private set; }

        public TrainModel() { } // Construtor vazio necessário para o MongoDB

        public TrainModel(string name, Guid userId, Guid personalId, List<ExerciseModel> exercises)
        {
            Id = Guid.NewGuid();
            Name = name;
            UserId = userId;
            PersonalId = personalId;
            Exercises = exercises;
        }
    }
}
