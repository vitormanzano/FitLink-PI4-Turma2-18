using FitLink.Dtos.Train;
using FitLink.Exceptions.Train;
using FitLink.Exceptions.User;
using FitLink.Services.Train;
using Microsoft.AspNetCore.Mvc;

namespace FitLink.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class TrainController : Controller
    {
        private readonly ITrainService _trainService;

        public TrainController(ITrainService trainService)
        {
            _trainService = trainService;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterTrainDto registerTrainDto)
        {
            try
            {
                var train = await _trainService.Register(registerTrainDto);
                return Ok(train);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex),
                };
            }
        }

        [HttpGet("getById/{trainId}")]
        public async Task<IActionResult> GetById([FromRoute] string trainId)
        {
            try
            {
                var train = await _trainService.GetTrainById(trainId);
                return Ok(train);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    TrainNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex),
                };
            }
        }

        [HttpGet("getByClientId/{clientId}")]
        public async Task<IActionResult> GetByClientId([FromRoute] string clientId)
        {
            try
            {
                var trains = await _trainService.GetTrainsByClientId(clientId);
                return Ok(trains);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    TrainNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex),
                };
            }
        }

        [HttpGet("getTrainsByPersonalId/{personalId}")]
        public async Task<IActionResult> GetTrainsByPersonalId([FromRoute] string personalId)
        {
            try
            {
                var trains = await _trainService.GetTrainsByPersonalId(personalId);
                return Ok(trains);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    TrainNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex),
                };
            }
        }

        [HttpPatch("update/{trainId}")]
        public async Task<IActionResult> Update([FromRoute] string trainId, [FromBody] UpdateTrainDto updateTrainDto)
        {
            try
            {
                var updatedTrain = await _trainService.UpdateTrainById(trainId, updateTrainDto);
                return Ok(updatedTrain);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    TrainNotFoundException => NotFound(ex.Message),
                    UserNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex),
                };
            }
        }

        [HttpDelete("deleteById/{trainId}")]
        public async Task<IActionResult> DeleteById([FromRoute] string trainId)
        {
            try
            {
                await _trainService.DeleteTrainById(trainId);
                return Ok();
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    TrainNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex),
                };
            }
        }
    }
}
