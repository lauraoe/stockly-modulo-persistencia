package co.sena.stockly.excepcion;

/**
 * Excepcion de tipo "unchecked" utilizada para envolver los errores
 * de acceso a datos (SQLException) que ocurren en la capa DAO,
 * evitando exponer detalles de JDBC a las capas superiores de la
 * aplicacion (por ejemplo, la capa de presentacion/consola).
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
