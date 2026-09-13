package co.sena.stockly.dao.impl;

import co.sena.stockly.conexion.ConexionBD;
import co.sena.stockly.dao.ProveedorDao;
import co.sena.stockly.excepcion.PersistenciaException;
import co.sena.stockly.modelo.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC del DAO de Proveedor. Ejecuta sentencias SQL
 * preparadas sobre la tabla "proveedores" de la base de datos
 * stockly_db.
 */
public class ProveedorDaoImpl implements ProveedorDao {

    private static final String SQL_INSERTAR =
            "INSERT INTO proveedores (nombre_proveedor, nit, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_ACTUALIZAR =
            "UPDATE proveedores SET nombre_proveedor = ?, nit = ?, telefono = ?, correo = ?, direccion = ? " +
                    "WHERE id_proveedor = ?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM proveedores WHERE id_proveedor = ?";
    private static final String SQL_CONSULTAR_POR_ID =
            "SELECT id_proveedor, nombre_proveedor, nit, telefono, correo, direccion " +
                    "FROM proveedores WHERE id_proveedor = ?";
    private static final String SQL_CONSULTAR_TODOS =
            "SELECT id_proveedor, nombre_proveedor, nit, telefono, correo, direccion " +
                    "FROM proveedores ORDER BY nombre_proveedor";

    @Override
    public Integer insertar(Proveedor proveedor) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, proveedor.getNombreProveedor());
            sentencia.setString(2, proveedor.getNit());
            sentencia.setString(3, proveedor.getTelefono());
            sentencia.setString(4, proveedor.getCorreo());
            sentencia.setString(5, proveedor.getDireccion());
            sentencia.executeUpdate();

            try (ResultSet llaveGenerada = sentencia.getGeneratedKeys()) {
                if (llaveGenerada.next()) {
                    int idGenerado = llaveGenerada.getInt(1);
                    proveedor.setIdProveedor(idGenerado);
                    return idGenerado;
                }
            }
            return null;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error insertando el proveedor: " + proveedor.getNombreProveedor(), excepcion);
        }
    }

    @Override
    public boolean actualizar(Proveedor proveedor) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setString(1, proveedor.getNombreProveedor());
            sentencia.setString(2, proveedor.getNit());
            sentencia.setString(3, proveedor.getTelefono());
            sentencia.setString(4, proveedor.getCorreo());
            sentencia.setString(5, proveedor.getDireccion());
            sentencia.setInt(6, proveedor.getIdProveedor());
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error actualizando el proveedor con id: " + proveedor.getIdProveedor(), excepcion);
        }
    }

    @Override
    public boolean eliminar(Integer idProveedor) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {
            sentencia.setInt(1, idProveedor);
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error eliminando el proveedor con id: " + idProveedor, excepcion);
        }
    }

    @Override
    public Proveedor consultarPorId(Integer idProveedor) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_POR_ID)) {
            sentencia.setInt(1, idProveedor);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearProveedor(resultado);
                }
            }
            return null;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando el proveedor con id: " + idProveedor, excepcion);
        }
    }

    @Override
    public List<Proveedor> consultarTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_TODOS);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                proveedores.add(mapearProveedor(resultado));
            }
            return proveedores;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando el listado de proveedores.", excepcion);
        }
    }

    /**
     * Convierte la fila actual de un ResultSet en un objeto Proveedor.
     */
    private Proveedor mapearProveedor(ResultSet resultado) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(resultado.getInt("id_proveedor"));
        proveedor.setNombreProveedor(resultado.getString("nombre_proveedor"));
        proveedor.setNit(resultado.getString("nit"));
        proveedor.setTelefono(resultado.getString("telefono"));
        proveedor.setCorreo(resultado.getString("correo"));
        proveedor.setDireccion(resultado.getString("direccion"));
        return proveedor;
    }
}
