using MongoDB.Bson.Serialization.Attributes;

namespace FitLink.Models
{
    public class UserModel
    {
        [BsonId]
        [BsonRepresentation(MongoDB.Bson.BsonType.String)] // Salva o Guid como string
        public Guid Id { get; private set; }
        public string Name { get; private set; }
        public string Email { get; private set; }
        public string HashedPassword { get; private set; }
        public bool IsPersonalTrainer { get; private set; }

        public UserModel(string name, string email, string hashedPassword, bool isPersonalTrainer)
        {
            Id = Guid.NewGuid();
            Name = name;
            Email = email;
            HashedPassword = hashedPassword;
            IsPersonalTrainer = isPersonalTrainer;
        }
    }
}
