using Microsoft.AspNetCore.Connections;
using Microsoft.Extensions.Primitives;
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
        public string Phone { get; private set; }
        public string City { get; private set; }
        public Guid? personalId = null;

        public UserModel() { } // Construtor vazio necessário para o MongoDB

        public UserModel(string name, string email, string hashedPassword, string phone, string city)
        {
            Id = Guid.NewGuid();
            Name = name;
            Email = email;
            HashedPassword = hashedPassword;
            Phone = phone;
            City = city;       
        }
    }
}
