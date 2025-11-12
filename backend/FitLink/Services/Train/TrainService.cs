using FitLink.Dtos.Train;
using FitLink.Exceptions.Train;
using FitLink.Exceptions.User;
using FitLink.Models;
using FitLink.Repository.Client;
using FitLink.Repository.Personal;
using FitLink.Repository.Train;

namespace FitLink.Services.Train
{
    public class TrainService : ITrainService
    {
        private readonly IClientRepository _clientRepository;
        private readonly IPersonalRepository _personalRepository;
        private readonly ITrainRepository _trainRepository;

        public TrainService(IClientRepository clientRepository, IPersonalRepository personalRepository, ITrainRepository trainRepository)
        {
            _clientRepository = clientRepository;
            _personalRepository = personalRepository;
            _trainRepository = trainRepository;
        }

        public async Task Register(RegisterTrainDto registerTrainDto)
        {
            var clientExist = await _clientRepository.GetDocumentByIdAsync(registerTrainDto.ClientId);

            if (clientExist == null) 
                throw new UserNotFoundException();

            var personalExist = await _personalRepository.GetDocumentByIdAsync(registerTrainDto.PersonalId);

            if (personalExist == null)
                throw new UserNotFoundException("Personal não encontrado!");

            var exercises = registerTrainDto.Exercises.Select(e => // Create the exercises
                new ExerciseModel(
                    e.Name,
                    e.Instructions,
                    [.. e.Sets.Select(s => new SetModel(
                        s.Number,
                        s.NumberOfRepetitions,
                        s.Weight))
                    ]
                )
            ).ToList();

            var train = new TrainModel(
                registerTrainDto.Name,
                registerTrainDto.ClientId,
                registerTrainDto.PersonalId,
                exercises);

            await _trainRepository.InsertDocumentAsync(train);
        }

        public async Task<ResponseTrainDto> GetTrainById(string trainId)
        {
            var train = await _trainRepository.GetDocumentByIdAsync(trainId);

            if (train is null)
                throw new TrainNotFoundException();

            var trainResponse = new ResponseTrainDto
            (
                train.Id,
                train.Name,
                train.ClientId,
                train.PersonalId,
                train.Exercises.Select(e => new ResponseExerciseDto
                (
                    e.Name,
                    e.Instructions,
                    e.Sets.Select(s => new ResponseSetDto
                    (
                        s.Number,
                        s.NumberOfRepetitions,
                        s.Weight
                    )).ToList()
                )).ToList()
            );

            return trainResponse;
        }
    }
}
