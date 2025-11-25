using FitLink.Dtos.Message;
using FitLink.Dtos.Personal;
using FitLink.Exceptions.Message;
using FitLink.Exceptions.User;
using FitLink.Models;
using FitLink.Repository.Client;
using FitLink.Repository.Message;
using FitLink.Repository.Personal;

namespace FitLink.Services.Message
{
    public class MessageService : IMessageService
    {
        private readonly IMessageRepository  _messageRepository;
        private readonly IClientRepository   _clientRepository;
        private readonly IPersonalRepository _personalRepository;

        public MessageService(
            IMessageRepository  messageRepository,
            IClientRepository   clientRepository,
            IPersonalRepository personalRepository)
        {
            _messageRepository  = messageRepository;
            _clientRepository   = clientRepository;
            _personalRepository = personalRepository;
        }

        public async Task<ResponseMessageDto> Register(RegisterMessageDto registerMessageDto)
        {
            var clientExist = _clientRepository.GetDocumentByIdAsync(registerMessageDto.ClientId);

            if (clientExist is null)
                throw new UserNotFoundException("Cliente não encontrado!");
            
            var personalExist = _personalRepository.GetDocumentByIdAsync(registerMessageDto.PersonalId);

            if (personalExist is null)
                throw new UserNotFoundException("Personal não encontrado!");
            
            var messageAlreadyExist = _messageRepository.GetByPersonalIdAndClientId(registerMessageDto.PersonalId, registerMessageDto.ClientId);

            if (messageAlreadyExist != null)
                throw new MessageAlreadyExistsException();

            var messageModel = new MessageModel
            (
                registerMessageDto.ClientId,
                registerMessageDto.PersonalId
            );

            await _messageRepository.InsertDocumentAsync(messageModel);

            var messageResponse = new ResponseMessageDto(
                messageModel.Id,
                messageModel.ClientId,
                messageModel.PersonalId,
                messageModel.HasAccepted);
            return messageResponse;
        }

        public async Task<MessageModel> GetById(string messageId)
        {
            var message = await _messageRepository.GetDocumentByIdAsync(messageId);

            if (message is null)
                throw new MessageNotFoundException("Mensagem não encontrada!");

            return message;
        }

        public async Task<IEnumerable<MessageModel>> GetAllMessagesByPersonal(string personalId)
        {
            var personalExist = _personalRepository.GetDocumentByIdAsync(personalId);

            if (personalExist is null)
                throw new UserNotFoundException("Personal não encontrado!");

            var messages = await _messageRepository.GetAllMessagesByPersonal(personalId);

            if (messages.Count() == 0)
                throw new MessageNotFoundException("Nenhuma mensagem encontrada para este personal!");

            var messagesResponse = messages.Select(message => new ResponseMessageDto
            (
                message.Id,
                message.ClientId,
                message.PersonalId,
                message.HasAccepted
            ));

            return messages;
        }

        public async Task Delete(string messageId)
        {
            var messageExist = _messageRepository.GetDocumentByIdAsync(messageId);

            if (messageExist is null)
                throw new MessageNotFoundException("Mensagem não encontrada!");

            await _messageRepository.DeleteDocumentAsync(p => p.Id.ToString() == messageId);
        }
    }
}
