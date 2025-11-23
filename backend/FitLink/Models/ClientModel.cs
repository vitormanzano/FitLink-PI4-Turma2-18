using Microsoft.AspNetCore.Connections;
using Microsoft.Extensions.Primitives;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace FitLink.Models
{
    public class ClientModel
    {
        [BsonId]
        [BsonRepresentation(BsonType.String)] // Salva o Guid como string
        public Guid Id { get; private set; }
        public string Name { get; private set; }
        public string Email { get; private set; }
        public string HashedPassword { get; private set; }
        public string Phone { get; private set; }
        public string City { get; private set; }
        [BsonRepresentation(BsonType.String)]
        public string? AboutMe { get; set; }
        public string? Goals { get; set; }
        public string? Metrics { get; set; }
        public string? PersonalId { get; private set; }

        public ClientModel() { } // Construtor vazio necessário para o MongoDB

        public ClientModel(string name, string email, string hashedPassword, string phone, string city)
        {
            Id = Guid.NewGuid();
            Name = name;
            Email = email;
            HashedPassword = hashedPassword;
            Phone = phone;
            City = city;
            AboutMe = null;
            Goals = null;
            Metrics = null;
            PersonalId = null;            
        }
    }
}
