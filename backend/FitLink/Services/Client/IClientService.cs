using FitLink.Dtos.Client;

namespace FitLink.Services.Client
{
    public interface IClientService
    {
        Task Register(RegisterClientDto registerClientDto);
        Task<ClientResponseDto> Login(LoginClientDto loginClientDto);
        Task<ClientResponseDto> GetClientById(string id);
        Task<IEnumerable<ClientResponseDto>> GetClientsByCity(string city);
        Task<IEnumerable<ClientResponseDto>> GetClientsByPersonalId(string personalId);
        Task<ClientResponseDto> Update(string id, UpdateClientDto updateClientDto);
        Task Delete(string id);
        Task LinkClientToPersonal(string clientId, string personalTrainerId);
        Task CloseLinkWithPersonal(string clientId);
    }
}
