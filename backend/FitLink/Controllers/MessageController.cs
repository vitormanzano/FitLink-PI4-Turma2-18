using FitLink.Dtos.Message;
using FitLink.Exceptions.Message;
using FitLink.Exceptions.User;
using FitLink.Services.Message;
using Microsoft.AspNetCore.Mvc;

namespace FitLink.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class MessageController : Controller
    {
        private readonly IMessageService _messageService;

        public MessageController(IMessageService messageService)
        {
            _messageService = messageService;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterMessageDto message)
        {
            try
            {
                var messageResponse = await _messageService.Register(message);
                return Ok(messageResponse);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }

        [HttpGet("getByMessageId/{messageId}")]
        public async Task<IActionResult> GetByMessageId([FromRoute] string messageId)
        {
            try
            {
                var message = await _messageService.GetById(messageId);
                return Ok(message);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    MessageNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }

        [HttpGet("GetAllMessagesByPersonalId/{personalId}")]
        public async Task<IActionResult> GetAllMessagesByPersonalId([FromRoute] string personalId)
        {
            try
            {
                var messages = await _messageService.GetAllMessagesByPersonal(personalId);
                return Ok(messages);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    MessageNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }

        [HttpDelete("delete/{messageId}")]
        public async Task<IActionResult> Delete([FromRoute] string messageId)
        {
            try
            {
                await _messageService.Delete(messageId);
                return Ok("Mensagem deletada com sucesso!");
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    MessageNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }
    }
}
