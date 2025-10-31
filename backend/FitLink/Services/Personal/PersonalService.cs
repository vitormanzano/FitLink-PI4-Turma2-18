using FitLink.Dtos.Personal;
using FitLink.Exceptions.User;
using FitLink.Models;
using FitLink.PasswordHasher;
using FitLink.Repository.Personal;

namespace FitLink.Services.Personal
{
    public class PersonalService : IPersonalService
    {
        private readonly IPersonalRepository _personalRepository;
        private readonly IPasswordHasher _passwordHasher;

        public PersonalService(IPersonalRepository personalRepository, IPasswordHasher passwordHasher)
        {
            _personalRepository = personalRepository;
            _passwordHasher = passwordHasher;
        }

        public async Task Register(RegisterPersonalDto registerPersonalDto)
        {
            var personalExist = await _personalRepository.GetPersonalByEmailAsync(registerPersonalDto.Email);

            if (personalExist != null)
                throw new UserAlreadyExist();

            var hashPassword = _passwordHasher.Hash(registerPersonalDto.Password);

            var personal = new PersonalTrainer(
                registerPersonalDto.Name,
                registerPersonalDto.Email,
                hashPassword,
                registerPersonalDto.Phone,
                registerPersonalDto.City,
                registerPersonalDto.Cpf,
                registerPersonalDto.Cref
            );

            await _personalRepository.InsertDocumentAsync(personal);
        }
    }
}
