using FitLink.Dtos.Train;
using FitLink.Models;

namespace FitLink.Mappers.Train
{
    public static class TrainMapper
    {
        public static ResponseTrainDto ModelToResponseDto(this TrainModel train)
        {
            return new ResponseTrainDto
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
        }
    }
}
