using FitLink.Dtos.Message;
using FitLink.Models;

namespace FitLink.Services.Message
{
    public interface IMessageService
    {
        Task<ResponseMessageDto> Register(RegisterMessageDto registerMessageDto);
        Task<MessageModel> GetById(string messageId);
        Task<IEnumerable<MessageModel>> GetAllMessagesByPersonal(string personalId);
        Task Delete(string messageId);
    }
}
