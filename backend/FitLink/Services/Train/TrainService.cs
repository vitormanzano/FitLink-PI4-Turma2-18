using FitLink.Dtos.Client;
using FitLink.Dtos.Train;
using FitLink.Exceptions.Train;
using FitLink.Exceptions.User;
using FitLink.Mappers.Train;
using FitLink.Models;
using FitLink.Repository.Client;
using FitLink.Repository.Personal;
using FitLink.Repository.Train;
using MongoDB.Driver;

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

        public async Task<ResponseTrainDto> Register(RegisterTrainDto registerTrainDto)
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

            return train.ModelToResponseDto();
        }

        public async Task<ResponseTrainDto> GetTrainById(string trainId)
        {
            var train = await _trainRepository.GetDocumentByIdAsync(trainId);

            if (train is null)
                throw new TrainNotFoundException();

            return train.ModelToResponseDto();
        }

        public async Task<ResponseTrainDto> GetTrainByClientId(string clientId)
        {
            var client = await _clientRepository.GetDocumentByIdAsync(clientId);

            if (client is null)
                throw new UserNotFoundException();

            var train = await _trainRepository.GetTrainByClientId(clientId);

            if (train is null)
                throw new TrainNotFoundException();

            return train.ModelToResponseDto();
        }

        public async Task<List<ResponseTrainDto>> GetTrainsByPersonalId(string personalId)
        {
            var personal = await _personalRepository.GetDocumentByIdAsync(personalId);

            if (personal is null)
                throw new UserNotFoundException("Personal não encontrado!");

            var trains = await _trainRepository.GetTrainsByPersonalId(personalId);

            if (trains.Count == 0)
                throw new TrainNotFoundException("Nenhum treino encontrado para este personal!");

            return trains.Select(t => t.ModelToResponseDto()).ToList();
        }

        public async Task<ResponseTrainDto> UpdateTrainById(string trainId, UpdateTrainDto updateTrainDto)
        {
            var train = _trainRepository.GetDocumentByIdAsync(trainId).Result;

            if (train is null)
                throw new TrainNotFoundException();

            await _trainRepository.UpdateDocumentAsync(
                t => t.Id.ToString() == (trainId),
                Builders<TrainModel>.Update
                    .Set(t => t.Name, updateTrainDto.Name)
                    .Set(t => t.Exercises, updateTrainDto.Exercises.Select(e => // Atualizando exercícios
                        new ExerciseModel(
                            e.Name,
                            e.Instructions,
                            [.. e.Sets.Select(s => new SetModel(
                                s.Number,
                                s.NumberOfRepetitions,
                                s.Weight))
                            ]
                        )
                    ).ToList())
            );

            var updatedTrain = await _trainRepository.GetDocumentByIdAsync(trainId);
            return updatedTrain.ModelToResponseDto();
        }

        public async Task DeleteTrainById(string trainId)
        {
            var train = await _trainRepository.GetDocumentByIdAsync(trainId);

            if (train is null)
                throw new TrainNotFoundException();

            await _trainRepository.DeleteDocumentAsync(t => t.Id.ToString() == trainId);
        }
    }
}
