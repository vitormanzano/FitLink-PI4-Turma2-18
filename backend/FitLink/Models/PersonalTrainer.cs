using MongoDB.Bson.Serialization.Attributes;

namespace FitLink.Models
{
    public class PersonalTrainer
    {
        [BsonId]
        [BsonRepresentation(MongoDB.Bson.BsonType.String)] // Salva o Guid como string
        public Guid Id { get; private set; }
        public string Name { get; private set; }
        public string Email { get; private set; }
        public string Cpf { get; private set; }
        public string Cref { get; private set; }
        public string Phone { get; private set; }
        public string HashedPassword { get; private set; }
        public bool IsPersonalTrainer { get; private set; } = true;

        public PersonalTrainer(string name, string email, string cpf, string cref, string phone, string hashedPassword)
        {
            Id = Guid.NewGuid();
            Name = name;
            Email = email;
            Cpf = cpf;
            Cref = cref;
            Phone = phone;
            HashedPassword = hashedPassword;
        }
    }
}
