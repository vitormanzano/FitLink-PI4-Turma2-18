using Microsoft.AspNetCore.Connections;
using Microsoft.Extensions.Primitives;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace FitLink.Models
{
    public class UserModel
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
        public string? PersonalId { get; private set; }

        public UserModel() { } // Construtor vazio necessário para o MongoDB

        public UserModel(string name, string email, string hashedPassword, string phone, string city)
        {
            Id = Guid.NewGuid();
            Name = name;
            Email = email;
            HashedPassword = hashedPassword;
            Phone = phone;
            City = city;
            PersonalId = null;
        }
    }
}
