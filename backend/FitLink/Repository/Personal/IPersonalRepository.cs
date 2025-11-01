using FitLink.Models;
using FitLink.Repository.Core;

namespace FitLink.Repository.Personal
{
    public interface IPersonalRepository : IBaseRepository<PersonalTrainerModel> // Tem todos os métodos de IRepository, aqui fica os métodos de personal trainer específicos
    {
        Task<PersonalTrainerModel> GetPersonalByEmailAsync(string email);
        Task<IEnumerable<PersonalTrainerModel>> GetPersonalTrainersByCity(string city);
    }
}
