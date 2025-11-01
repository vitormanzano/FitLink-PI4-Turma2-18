using System.Security.Authentication;
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
                throw new UserAlreadyExistException();

            var hashPassword = _passwordHasher.Hash(registerPersonalDto.Password);

            var personal = new PersonalTrainerModel(
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

        public async Task<IEnumerable<ResponsePersonalDto>> GetPersonalTrainersByCity(string city)
        {
            var personals = await _personalRepository.GetPersonalTrainersByCity(city);

            var personalResponse = personals.Select(personal => new ResponsePersonalDto(
                personal.Id,
                personal.Name,
                personal.Phone,
                personal.City
            ));
               
            return personalResponse;
        }

        public async Task<ResponsePersonalDto> Login(LoginPersonalDto loginPersonalDto)
        {
            var personalExist = await _personalRepository.GetPersonalByEmailAsync(loginPersonalDto.Email);
            
            if (personalExist == null)
                throw new UserNotFoundException();
            
            var passwordMatch = _passwordHasher.Verify(loginPersonalDto.Password, personalExist.HashedPassword);
            
            if (!passwordMatch)
                throw new InvalidCredentialException("Credenciais inválidas!");
            
            return new ResponsePersonalDto(personalExist.Id, personalExist.Name, personalExist.Phone, personalExist.City);
        }
    }
}
