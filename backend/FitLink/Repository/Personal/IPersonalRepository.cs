using FitLink.Models;
using FitLink.Repository.Core;

namespace FitLink.Repository.Personal
{
    public interface IPersonalRepository : IBaseRepository<PersonalTrainer> // Tem todos os métodos de IRepository, aqui fica os métodos de personal trainer específicos
    {
        Task<PersonalTrainer> GetPersonalByEmailAsync(string email);
    }
}
