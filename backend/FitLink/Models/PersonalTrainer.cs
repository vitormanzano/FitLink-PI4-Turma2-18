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
        public string HashedPassword { get; private set; }
        public string Phone { get; private set; }
        public string City { get; private set; }
        public string Cpf { get; private set; }
        public string Cref { get; private set; }
              
        public PersonalTrainer() { } // Construtor vazio para o MongoDB

        public PersonalTrainer(string name, string email, string hashedPassword, string phone, string city, string cpf, string cref)
        {
            Id = Guid.NewGuid();
            Name = name;
            Email = email;
            HashedPassword = hashedPassword;
            Phone = phone;
            City = city;
            Cpf = cpf;
            Cref = cref;
        }
    }
}
