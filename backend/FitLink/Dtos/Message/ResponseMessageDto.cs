namespace FitLink.Dtos.Message
{
    public record ResponseMessageDto(
        Guid Id,
        string ClientId,
        string PersonalId,
        bool HasAccepted);
}
