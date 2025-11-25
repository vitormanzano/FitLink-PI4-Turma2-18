using FitLink.Models;
using FitLink.Repository.Core;

namespace FitLink.Repository.Message
{
    public interface IMessageRepository : IBaseRepository<MessageModel>
    {
        Task<IEnumerable<MessageModel>> GetAllMessagesByPersonal(string personalId);
        Task<MessageModel> GetByPersonalIdAndClientId(string clientId, string personalId);
    }
}
