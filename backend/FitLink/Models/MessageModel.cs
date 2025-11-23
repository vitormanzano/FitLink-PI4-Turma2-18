using MongoDB.Bson.Serialization.Attributes;

namespace FitLink.Models
{
    public class MessageModel
    {
        [BsonId]
        [BsonRepresentation(MongoDB.Bson.BsonType.String)] // Salva o Guid como string
        public Guid Id { get; private set; }
        public string ClientId { get; private set; } // ID do cliente
        public string PersonalId { get; private set; } // ID do personal trainer
        public bool HasAccepted { get; private set; } // Indica se a solicitação foi aceita

        public MessageModel() { } // Construtor vazio necessário para o MongoDB
        
        public MessageModel(string clientId, string personalId)
        {
            Id = Guid.NewGuid();
            ClientId = clientId;
            PersonalId = personalId;
            HasAccepted = false;
        }
    }
}
