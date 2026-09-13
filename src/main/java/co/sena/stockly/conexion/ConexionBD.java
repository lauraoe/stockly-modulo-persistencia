package co.sena.stockly.conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase encargada de administrar la conexion JDBC hacia la base de
 * datos MySQL del proyecto Stockly (stockly_db).
 *
 * Implementa el patron Singleton: mantiene una unica conexion activa
 * y la reutiliza durante la ejecucion de la aplicacion. Los datos de
 * conexion (url, usuario, clave, driver) se leen desde el archivo
 * de configuracion config.properties, ubicado en el classpath, para
 * evitar dejar credenciales escritas directamente en el codigo fuente.
 *
 * @author Stockly - Modulo de persistencia
 */
public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION = "config.properties";
    private static ConexionBD instancia;

    private Connection conexion;

    /**
     * Constructor privado: obliga a obtener la unica instancia disponible
     * a traves del metodo obtenerInstancia().
     */
    private ConexionBD() {
        inicializarConexion();
    }

    /**
     * Retorna la unica instancia de ConexionBD (patron Singleton),
     * creandola la primera vez que se solicita.
     *
     * @return instancia unica de ConexionBD
     */
    public static synchronized ConexionBD obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Entrega la conexion JDBC activa, reabriendola en caso de que
     * se haya cerrado previamente.
     *
     * @return objeto Connection listo para ejecutar sentencias SQL
     */
    public Connection obtenerConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                inicializarConexion();
            }
        } catch (SQLException excepcion) {
            throw new RuntimeException("No fue posible validar el estado de la conexion.", excepcion);
        }
        return conexion;
    }

    /**
     * Lee el archivo config.properties, carga el driver JDBC y abre
     * la conexion contra la base de datos stockly_db.
     */
    private void inicializarConexion() {
        Properties propiedades = cargarPropiedades();
        String url = propiedades.getProperty("db.url");
        String usuario = propiedades.getProperty("db.usuario");
        String clave = propiedades.getProperty("db.clave");
        String driver = propiedades.getProperty("db.driver");

        try {
            Class.forName(driver);
            this.conexion = DriverManager.getConnection(url, usuario, clave);
        } catch (ClassNotFoundException excepcion) {
            throw new RuntimeException("No se encontro el driver JDBC configurado: " + driver, excepcion);
        } catch (SQLException excepcion) {
            throw new RuntimeException("No fue posible conectar con la base de datos stockly_db.", excepcion);
        }
    }

    /**
     * Carga las propiedades de conexion desde el classpath.
     *
     * @return objeto Properties con los datos de conexion
     */
    private Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        try (InputStream flujoEntrada = ConexionBD.class.getClassLoader()
                .getResourceAsStream(ARCHIVO_CONFIGURACION)) {
            if (flujoEntrada == null) {
                throw new RuntimeException("No se encontro el archivo " + ARCHIVO_CONFIGURACION + " en el classpath.");
            }
            propiedades.load(flujoEntrada);
        } catch (IOException excepcion) {
            throw new RuntimeException("Error leyendo el archivo " + ARCHIVO_CONFIGURACION, excepcion);
        }
        return propiedades;
    }

    /**
     * Cierra la conexion activa con la base de datos, si existe.
     */
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException excepcion) {
            throw new RuntimeException("Error cerrando la conexion con la base de datos.", excepcion);
        }
    }
}
