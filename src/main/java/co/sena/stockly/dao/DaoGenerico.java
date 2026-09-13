package co.sena.stockly.dao;

import java.util.List;

/**
 * Contrato generico que deben cumplir todos los DAO (Data Access
 * Object) del modulo de persistencia de Stockly. Define las cuatro
 * operaciones basicas de acceso a datos: insercion, consulta,
 * actualizacion y eliminacion (CRUD).
 *
 * @param <T>  tipo de entidad (modelo) que administra el DAO
 * @param <ID> tipo del identificador (llave primaria) de la entidad
 */
public interface DaoGenerico<T, ID> {

    /**
     * Inserta una nueva entidad en la base de datos.
     *
     * @param entidad objeto a insertar
     * @return el identificador autogenerado por la base de datos
     */
    ID insertar(T entidad);

    /**
     * Actualiza los datos de una entidad ya existente.
     *
     * @param entidad objeto con los datos actualizados (debe incluir el id)
     * @return true si la actualizacion afecto al menos un registro
     */
    boolean actualizar(T entidad);

    /**
     * Elimina una entidad de la base de datos a partir de su identificador.
     *
     * @param id identificador de la entidad a eliminar
     * @return true si la eliminacion afecto al menos un registro
     */
    boolean eliminar(ID id);

    /**
     * Consulta una entidad especifica a partir de su identificador.
     *
     * @param id identificador de la entidad buscada
     * @return la entidad encontrada, o null si no existe
     */
    T consultarPorId(ID id);

    /**
     * Consulta el listado completo de entidades registradas.
     *
     * @return lista con todas las entidades encontradas
     */
    List<T> consultarTodos();
}
