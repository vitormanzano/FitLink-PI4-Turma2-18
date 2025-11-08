using FitLink.Dtos.Train;
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

        [HttpPost("Register")]
        public async Task<IActionResult> Register([FromBody] RegisterTrainDto registerTrainDto)
        {
            try
            {
                await _trainService.Register(registerTrainDto);
                return Ok();
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex),
                    _ => BadRequest(ex),
                };
            }
        }
    }
}
