package co.sena.stockly.dao.impl;

import co.sena.stockly.conexion.ConexionBD;
import co.sena.stockly.dao.CategoriaDao;
import co.sena.stockly.excepcion.PersistenciaException;
import co.sena.stockly.modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC del DAO de Categoria. Ejecuta sentencias SQL
 * preparadas (PreparedStatement) sobre la tabla "categorias" de la
 * base de datos stockly_db.
 */
public class CategoriaDaoImpl implements CategoriaDao {

    private static final String SQL_INSERTAR =
            "INSERT INTO categorias (nombre_categoria, descripcion) VALUES (?, ?)";
    private static final String SQL_ACTUALIZAR =
            "UPDATE categorias SET nombre_categoria = ?, descripcion = ? WHERE id_categoria = ?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM categorias WHERE id_categoria = ?";
    private static final String SQL_CONSULTAR_POR_ID =
            "SELECT id_categoria, nombre_categoria, descripcion FROM categorias WHERE id_categoria = ?";
    private static final String SQL_CONSULTAR_TODOS =
            "SELECT id_categoria, nombre_categoria, descripcion FROM categorias ORDER BY nombre_categoria";

    @Override
    public Integer insertar(Categoria categoria) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, categoria.getNombreCategoria());
            sentencia.setString(2, categoria.getDescripcion());
            sentencia.executeUpdate();

            try (ResultSet llaveGenerada = sentencia.getGeneratedKeys()) {
                if (llaveGenerada.next()) {
                    int idGenerado = llaveGenerada.getInt(1);
                    categoria.setIdCategoria(idGenerado);
                    return idGenerado;
                }
            }
            return null;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error insertando la categoria: " + categoria.getNombreCategoria(), excepcion);
        }
    }

    @Override
    public boolean actualizar(Categoria categoria) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setString(1, categoria.getNombreCategoria());
            sentencia.setString(2, categoria.getDescripcion());
            sentencia.setInt(3, categoria.getIdCategoria());
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error actualizando la categoria con id: " + categoria.getIdCategoria(), excepcion);
        }
    }

    @Override
    public boolean eliminar(Integer idCategoria) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {
            sentencia.setInt(1, idCategoria);
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error eliminando la categoria con id: " + idCategoria, excepcion);
        }
    }

    @Override
    public Categoria consultarPorId(Integer idCategoria) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_POR_ID)) {
            sentencia.setInt(1, idCategoria);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearCategoria(resultado);
                }
            }
            return null;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando la categoria con id: " + idCategoria, excepcion);
        }
    }

    @Override
    public List<Categoria> consultarTodos() {
        List<Categoria> categorias = new ArrayList<>();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_TODOS);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                categorias.add(mapearCategoria(resultado));
            }
            return categorias;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando el listado de categorias.", excepcion);
        }
    }

    /**
     * Convierte la fila actual de un ResultSet en un objeto Categoria.
     */
    private Categoria mapearCategoria(ResultSet resultado) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(resultado.getInt("id_categoria"));
        categoria.setNombreCategoria(resultado.getString("nombre_categoria"));
        categoria.setDescripcion(resultado.getString("descripcion"));
        return categoria;
    }
}
