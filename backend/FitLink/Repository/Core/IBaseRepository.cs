using System.Linq.Expressions;
using MongoDB.Driver;

namespace FitLink.Repository.Core
{
    public interface IBaseRepository<T> where T : class
    {
        Task InsertDocumentAsync(T model);
        Task<T> GetDocumentByIdAsync(string id);
        Task UpdateDocumentAsync(Expression<Func<T, bool>> filterExpression, UpdateDefinition<T> update);
        Task DeleteDocumentAsync(Expression<Func<T, bool>> filterExpression);
    }
}
