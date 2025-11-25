using FitLink.Models;
using FitLink.Repository.Core;
using MongoDB.Driver;

namespace FitLink.Repository.Message
{
    public class MessageRepository : BaseRepository<MessageModel>, IMessageRepository
    {
       public MessageRepository(IMongoDatabase database) : base(database, "messages")
       {
       }

        public async Task<IEnumerable<MessageModel>> GetAllMessagesByPersonal(string personalId)
        {
            var filter = Builders<MessageModel>.Filter.Eq(p => p.PersonalId, personalId);
            return await _collection.Find(filter)
                .ToListAsync();
        }
    }
}
