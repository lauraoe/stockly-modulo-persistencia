package co.sena.stockly.dao.impl;

import co.sena.stockly.conexion.ConexionBD;
import co.sena.stockly.dao.VarianteProductoDao;
import co.sena.stockly.excepcion.PersistenciaException;
import co.sena.stockly.modelo.VarianteProducto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC del DAO de VarianteProducto. Ejecuta sentencias
 * SQL preparadas sobre la tabla "variantes_producto", relacionandola
 * con "productos", "tallas" y "colores" para obtener la informacion
 * completa que necesita la pantalla de inventario del aplicativo.
 *
 * Nota: el campo stock_actual solo debe modificarse a traves de los
 * triggers de la base de datos (trg_entradas_after_insert /
 * trg_salidas_after_insert) al registrar movimientos de inventario;
 * por eso este DAO no expone una actualizacion directa de stock.
 */
public class VarianteProductoDaoImpl implements VarianteProductoDao {

    private static final String SQL_INSERTAR =
            "INSERT INTO variantes_producto (id_producto, id_talla, id_color, sku, codigo_qr, " +
                    "stock_actual, stock_minimo) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE variantes_producto SET id_producto = ?, id_talla = ?, id_color = ?, sku = ?, " +
                    "codigo_qr = ?, stock_minimo = ? WHERE id_variante = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM variantes_producto WHERE id_variante = ?";

    private static final String SQL_BASE_CONSULTA =
            "SELECT vp.id_variante, vp.id_producto, p.nombre_producto, vp.id_talla, t.nombre_talla, " +
                    "vp.id_color, c.nombre_color, vp.sku, vp.codigo_qr, vp.stock_actual, vp.stock_minimo " +
                    "FROM variantes_producto vp " +
                    "INNER JOIN productos p ON p.id_producto = vp.id_producto " +
                    "INNER JOIN tallas t ON t.id_talla = vp.id_talla " +
                    "INNER JOIN colores c ON c.id_color = vp.id_color ";

    private static final String SQL_CONSULTAR_POR_ID = SQL_BASE_CONSULTA + "WHERE vp.id_variante = ?";
    private static final String SQL_CONSULTAR_TODOS = SQL_BASE_CONSULTA + "ORDER BY p.nombre_producto";
    private static final String SQL_CONSULTAR_POR_QR = SQL_BASE_CONSULTA + "WHERE vp.codigo_qr = ?";
    private static final String SQL_CONSULTAR_STOCK_BAJO = SQL_BASE_CONSULTA + "WHERE vp.stock_actual <= vp.stock_minimo";

    @Override
    public Integer insertar(VarianteProducto variante) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, variante.getIdProducto());
            sentencia.setInt(2, variante.getIdTalla());
            sentencia.setInt(3, variante.getIdColor());
            sentencia.setString(4, variante.getSku());
            sentencia.setString(5, variante.getCodigoQr());
            sentencia.setInt(6, variante.getStockActual());
            sentencia.setInt(7, variante.getStockMinimo());
            sentencia.executeUpdate();

            try (ResultSet llaveGenerada = sentencia.getGeneratedKeys()) {
                if (llaveGenerada.next()) {
                    int idGenerado = llaveGenerada.getInt(1);
                    variante.setIdVariante(idGenerado);
                    return idGenerado;
                }
            }
            return null;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error insertando la variante con SKU: " + variante.getSku(), excepcion);
        }
    }

    @Override
    public boolean actualizar(VarianteProducto variante) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setInt(1, variante.getIdProducto());
            sentencia.setInt(2, variante.getIdTalla());
            sentencia.setInt(3, variante.getIdColor());
            sentencia.setString(4, variante.getSku());
            sentencia.setString(5, variante.getCodigoQr());
            sentencia.setInt(6, variante.getStockMinimo());
            sentencia.setInt(7, variante.getIdVariante());
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error actualizando la variante con id: " + variante.getIdVariante(), excepcion);
        }
    }

    @Override
    public boolean eliminar(Integer idVariante) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {
            sentencia.setInt(1, idVariante);
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException excepcion) {
            throw new PersistenciaException(
                    "Error eliminando la variante con id: " + idVariante +
                            ". Verifique que no tenga movimientos de inventario asociados.", excepcion);
        }
    }

    @Override
    public VarianteProducto consultarPorId(Integer idVariante) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_POR_ID)) {
            sentencia.setInt(1, idVariante);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearVariante(resultado);
                }
            }
            return null;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando la variante con id: " + idVariante, excepcion);
        }
    }

    @Override
    public List<VarianteProducto> consultarTodos() {
        List<VarianteProducto> variantes = new ArrayList<>();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_TODOS);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                variantes.add(mapearVariante(resultado));
            }
            return variantes;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando el listado de variantes de producto.", excepcion);
        }
    }

    @Override
    public VarianteProducto consultarPorCodigoQr(String codigoQr) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_POR_QR)) {
            sentencia.setString(1, codigoQr);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearVariante(resultado);
                }
            }
            return null;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando la variante con codigo QR: " + codigoQr, excepcion);
        }
    }

    @Override
    public List<VarianteProducto> consultarStockBajo() {
        List<VarianteProducto> variantes = new ArrayList<>();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_STOCK_BAJO);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                variantes.add(mapearVariante(resultado));
            }
            return variantes;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando las variantes con stock bajo.", excepcion);
        }
    }

    /**
     * Convierte la fila actual de un ResultSet en un objeto VarianteProducto.
     */
    private VarianteProducto mapearVariante(ResultSet resultado) throws SQLException {
        VarianteProducto variante = new VarianteProducto();
        variante.setIdVariante(resultado.getInt("id_variante"));
        variante.setIdProducto(resultado.getInt("id_producto"));
        variante.setNombreProducto(resultado.getString("nombre_producto"));
        variante.setIdTalla(resultado.getInt("id_talla"));
        variante.setNombreTalla(resultado.getString("nombre_talla"));
        variante.setIdColor(resultado.getInt("id_color"));
        variante.setNombreColor(resultado.getString("nombre_color"));
        variante.setSku(resultado.getString("sku"));
        variante.setCodigoQr(resultado.getString("codigo_qr"));
        variante.setStockActual(resultado.getInt("stock_actual"));
        variante.setStockMinimo(resultado.getInt("stock_minimo"));
        return variante;
    }
}
