using FitLink.PasswordHasher;
using FitLink.Repository.Personal;
using FitLink.Repository.User;
using FitLink.Services.Personal;
using FitLink.Services.User;
using FitLink.ServicesExtensions;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Adicionando mongo
builder.Services.AddMongo(builder.Configuration);

// Injeção de dependência
builder.Services.AddScoped<IUserService, UserService>(); // AddScoped : Cria uma instância por requisição
builder.Services.AddScoped<IUserRepository, UserRepository>(); 
builder.Services.AddScoped<IPasswordHasher, PasswordHasher>();
builder.Services.AddScoped<IPersonalService, PersonalService>();
builder.Services.AddScoped<IPersonalRepository, PersonalRepository>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
