using System.Security.Authentication;
using FitLink.Dtos.Client;
using FitLink.Exceptions.User;
using FitLink.Models;
using FitLink.PasswordHasher;
using FitLink.Repository.Personal;
using FitLink.Repository.Client;
using MongoDB.Driver;

namespace FitLink.Services.Client
{
    public class ClientService : IClientService
    {
        private readonly IClientRepository _clientRepository;
        private readonly IPersonalRepository _personalRepository;
        private readonly IPasswordHasher _passwordHasher;

        public ClientService(IClientRepository clientRepository, IPersonalRepository personalRepository, IPasswordHasher passwordHasher)
        {
            _clientRepository = clientRepository;
            _personalRepository = personalRepository;
            _passwordHasher = passwordHasher;
        }

        public async Task Register(RegisterClientDto registerClientDto)
        {
            var clientAlreadyExists = await _clientRepository.GetClientByEmailAsync(registerClientDto.Email);

            if (clientAlreadyExists != null)
                throw new UserAlreadyExistException();

            var hashPassword = _passwordHasher.Hash(registerClientDto.Password);

            var client = new ClientModel(registerClientDto.Name, registerClientDto.Email, hashPassword, registerClientDto.Phone, registerClientDto.City);
            await _clientRepository.InsertDocumentAsync(client);
        }

        public async Task<ClientResponseDto> Login(LoginClientDto loginClientDto)
        {
            var client = await _clientRepository.GetClientByEmailAsync(loginClientDto.Email);
            if (client == null)
                throw new UserNotFoundException();

            var isPasswordValid = _passwordHasher.Verify(loginClientDto.Password, client.HashedPassword);

            if (!isPasswordValid)
                throw new InvalidCredentialException("Credenciais inválidas!");

            return new ClientResponseDto(
                client.Id, 
                client.Name, 
                client.Email, 
                client.Phone, 
                client.City,
                client.AboutMe,
                client.Goals,
                client.Metrics
            );
        }

        public async Task<ClientResponseDto> GetClientById(string id)
        {
            var client = await _clientRepository.GetDocumentByIdAsync(id);

            if (client == null)
                throw new UserNotFoundException();

            var clientResponseDto = new ClientResponseDto(
                client.Id,
                client.Name,
                client.Email,
                client.Phone,
                client.City,
                client.AboutMe,
                client.Goals,
                client.Metrics
            );

            return clientResponseDto;
        }

        public async Task<IEnumerable<ClientResponseDto>> GetClientsByCity(string city)
        {
            var clients = await _clientRepository.GetClientsByCity(city);

            if (clients.Count() == 0)
                throw new UserNotFoundException();

            var clientsResponseDto = clients.Select(client => new ClientResponseDto(
                client.Id,
                client.Name,
                client.Email,
                client.Phone,
                client.City,
                client.AboutMe,
                client.Goals,
                client.Metrics
            ));

            return clientsResponseDto;
        }


        public async Task<IEnumerable<ClientResponseDto>> GetClientsByPersonalId(string personalId)
        {
            var personal = _personalRepository.GetDocumentByIdAsync(personalId);

            if (personal == null)
                throw new UserNotFoundException("Personal não encontrado!");

            var clients = await _clientRepository.GetClientsByPersonalId(personalId);

            if (clients.Count() == 0)
                throw new UserNotFoundException("Nenhum aluno encontrado!");

            var clientsResponseDto = clients.Select(client => new ClientResponseDto(
                client.Id,
                client.Name,
                client.Email,
                client.Phone,
                client.City,
                client.AboutMe,
                client.Goals,
                client.Metrics
            ));

            return clientsResponseDto;
        }

        public async Task<ClientResponseDto> Update(string id, UpdateClientDto updateClientDto)
        {
            var client = await _clientRepository.GetDocumentByIdAsync(id);

            if (client == null)
                throw new UserNotFoundException();

            await _clientRepository.UpdateDocumentAsync(
                u => u.Id.ToString() == (id),
                Builders<ClientModel>.Update
                    .Set(u => u.Name, updateClientDto.Name)
                    .Set(u => u.Email, updateClientDto.Email)
                    .Set(u => u.HashedPassword, _passwordHasher.Hash(updateClientDto.Password))
                    .Set(u => u.Phone, updateClientDto.Phone)
                    .Set(u => u.City, updateClientDto.City)
            );

            var clientResponse = new ClientResponseDto(
                client.Id,
                updateClientDto.Name,
                updateClientDto.Email,
                updateClientDto.Phone,
                updateClientDto.City,
                updateClientDto.AboutMe,
                updateClientDto.Goals,
                updateClientDto.Metrics
            );

            return clientResponse;
        }

        public async Task Delete(string id)
        {
            var client = _clientRepository.GetDocumentByIdAsync(id);

            if (client == null)
                throw new UserNotFoundException();

            await _clientRepository.DeleteDocumentAsync(p => p.Id.ToString() == id);
        }

        public async Task LinkClientToPersonal(string clientId, string personalTrainerId)
        {
            var client = await _clientRepository.GetDocumentByIdAsync(clientId);

            if (client is null)
                throw new UserNotFoundException();

            var personal = await _personalRepository.GetDocumentByIdAsync(personalTrainerId);

            if (personal is null)
                throw new UserNotFoundException("Personal não encontrado!");

            await _clientRepository.UpdateDocumentAsync(
                u => u.Id.ToString() == (clientId),
                Builders<ClientModel>.Update.Set(u => u.PersonalId, personalTrainerId));
        }

        public async Task CloseLinkWithPersonal(string clientId)
        {
            var client = await _clientRepository.GetDocumentByIdAsync(clientId);

            if (client is null)
                throw new UserNotFoundException();

            await _clientRepository.UpdateDocumentAsync(
                u => u.Id.ToString() == (clientId),
                Builders<ClientModel>.Update.Set(u => u.PersonalId, null));
        }

        public async Task AddInformations(string clientId, MoreInformations moreInformations)
        {
            var client = await _clientRepository.GetDocumentByIdAsync(clientId);

            if (client is null)
                throw new UserNotFoundException();

            await _clientRepository.UpdateDocumentAsync(
                u => u.Id.ToString() == (clientId),
                Builders<ClientModel>.Update
                    .Set(u => u.AboutMe, moreInformations.AboutMe)
                    .Set(u => u.Goals, moreInformations.Goals)
                    .Set(u => u.Metrics, moreInformations.Metrics));
        }
    }
}
