using System.Security.Authentication;
using FitLink.Dtos.Personal;
using FitLink.Exceptions.User;
using FitLink.Models;
using FitLink.PasswordHasher;
using FitLink.Repository.Personal;
using MongoDB.Driver;

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

            personalExist = await _personalRepository.GetPersonalTrainerByCpf(registerPersonalDto.Cpf);

            if (personalExist != null)
                throw new UserAlreadyExistException("CPF já cadastrado!");

            personalExist = await _personalRepository.GetPersonalTrainerByCref(registerPersonalDto.Cref);

            if (personalExist != null)
                throw new UserAlreadyExistException("CREF já cadastrado!");

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

        public async Task<ResponsePersonalDto> Login(LoginPersonalDto loginPersonalDto)
        {
            var personalExist = await _personalRepository.GetPersonalByEmailAsync(loginPersonalDto.Email);

            if (personalExist == null)
                throw new UserNotFoundException();

            var passwordMatch = _passwordHasher.Verify(loginPersonalDto.Password, personalExist.HashedPassword);

            if (!passwordMatch)
                throw new InvalidCredentialException("Credenciais inválidas!");

            return new ResponsePersonalDto(
                personalExist.Id,
                personalExist.Name,
                personalExist.Email,
                personalExist.Phone,
                personalExist.City);
        }

        public async Task<IEnumerable<ResponsePersonalDto>> GetPersonalTrainersByCity(string city)
        {
            var personals = await _personalRepository.GetPersonalTrainersByCity(city);

            var personalResponse = personals.Select(personal => new ResponsePersonalDto(
                personal.Id,
                personal.Name,
                personal.Email,
                personal.Phone,
                personal.City
            ));
               
            return personalResponse;
        }
     
        public async Task<ResponsePersonalDto> GetPersonalById(string personalId)
        {
            var personalExist = await _personalRepository.GetDocumentByIdAsync(personalId);
            
            if (personalExist == null)
                throw new UserNotFoundException();
            
            return new ResponsePersonalDto(
                personalExist.Id, 
                personalExist.Name, 
                personalExist.Email, 
                personalExist.Phone, 
                personalExist.City);
        }

        public async Task<ResponsePersonalDto> Update(string personalId, UpdatePersonalDto updatePersonalDto)
        {
            var personal = await _personalRepository.GetDocumentByIdAsync(personalId);

            if (personal == null)
                throw new UserNotFoundException();

            await _personalRepository.UpdateDocumentAsync(
                p => p.Id.ToString() == (personalId),
                Builders<PersonalTrainerModel>.Update
                    .Set(p => p.Name, updatePersonalDto.Name)
                    .Set(p => p.Email, updatePersonalDto.Email)
                    .Set(p => p.HashedPassword, _passwordHasher.Hash(updatePersonalDto.Password))
                    .Set(p => p.Phone, updatePersonalDto.Phone)
                    .Set(p => p.City, updatePersonalDto.City)
            );

            var personalResponse = new ResponsePersonalDto(
                personal.Id,
                updatePersonalDto.Name,
                updatePersonalDto.Email,
                updatePersonalDto.Phone,
                updatePersonalDto.City
            );

            return personalResponse;
        }

        public async Task Delete(string personalId)
        {
            var personal = _personalRepository.GetDocumentByIdAsync(personalId);

            if (personal == null)
                throw new UserNotFoundException();

            await _personalRepository.DeleteDocumentAsync(p => p.Id.ToString() == personalId);
        }
    }
}
