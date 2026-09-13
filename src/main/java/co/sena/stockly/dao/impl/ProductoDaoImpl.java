package co.sena.stockly.dao.impl;

import co.sena.stockly.conexion.ConexionBD;
import co.sena.stockly.dao.ProductoDao;
import co.sena.stockly.excepcion.PersistenciaException;
import co.sena.stockly.modelo.EstadoProducto;
import co.sena.stockly.modelo.Genero;
import co.sena.stockly.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC del DAO de Producto. Ejecuta sentencias SQL
 * preparadas sobre la tabla "productos", relacionandola con
 * "categorias" y "proveedores" para traer los nombres legibles en
 * las consultas (tal como se muestran en las pantallas de
 * inventario y detalle de producto del prototipo de Stockly).
 */
public class ProductoDaoImpl implements ProductoDao {

    private static final String SQL_INSERTAR =
            "INSERT INTO productos (nombre_producto, descripcion, marca, genero, precio_compra, " +
                    "precio_venta, id_categoria, id_proveedor, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE productos SET nombre_producto = ?, descripcion = ?, marca = ?, genero = ?, " +
                    "precio_compra = ?, precio_venta = ?, id_categoria = ?, id_proveedor = ?, estado = ? " +
                    "WHERE id_producto = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM productos WHERE id_producto = ?";

    private static final String SQL_BASE_CONSULTA =
            "SELECT p.id_producto, p.nombre_producto, p.descripcion, p.marca, p.genero, " +
                    "p.precio_compra, p.precio_venta, p.estado, p.fecha_registro, " +
                    "p.id_categoria, c.nombre_categoria, p.id_proveedor, pr.nombre_proveedor " +
                    "FROM productos p " +
                    "INNER JOIN categorias c ON c.id_categoria = p.id_categoria " +
                    "INNER JOIN proveedores pr ON pr.id_proveedor = p.id_proveedor ";

    private static final String SQL_CONSULTAR_POR_ID = SQL_BASE_CONSULTA + "WHERE p.id_producto = ?";
    private static final String SQL_CONSULTAR_TODOS = SQL_BASE_CONSULTA + "ORDER BY p.nombre_producto";
    private static final String SQL_BUSCAR_POR_NOMBRE = SQL_BASE_CONSULTA +
            "WHERE p.nombre_producto LIKE ? ORDER BY p.nombre_producto";

    @Override
    public Integer insertar(Producto producto) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, producto.getNombreProducto());
            sentencia.setString(2, producto.getDescripcion());
            sentencia.setString(3, producto.getMarca());
            sentencia.setString(4, producto.getGenero().name());
            sentencia.setBigDecimal(5, producto.getPrecioCompra());
            sentencia.setBigDecimal(6, producto.getPrecioVenta());
            sentencia.setInt(7, producto.getIdCategoria());
            sentencia.setInt(8, producto.getIdProveedor());
            sentencia.setString(9, producto.getEstado() != null ? producto.getEstado().name() : EstadoProducto.ACTIVO.name());
            sentencia.executeUpdate();

            try (ResultSet llaveGenerada = sentencia.getGeneratedKeys()) {
                if (llaveGenerada.next()) {
                    int idGenerado = llaveGenerada.getInt(1);
                    producto.setIdProducto(idGenerado);
                    return idGenerado;
                }
            }
            return null;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error insertando el producto: " + producto.getNombreProducto(), excepcion);
        }
    }

    @Override
    public boolean actualizar(Producto producto) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setString(1, producto.getNombreProducto());
            sentencia.setString(2, producto.getDescripcion());
            sentencia.setString(3, producto.getMarca());
            sentencia.setString(4, producto.getGenero().name());
            sentencia.setBigDecimal(5, producto.getPrecioCompra());
            sentencia.setBigDecimal(6, producto.getPrecioVenta());
            sentencia.setInt(7, producto.getIdCategoria());
            sentencia.setInt(8, producto.getIdProveedor());
            sentencia.setString(9, producto.getEstado().name());
            sentencia.setInt(10, producto.getIdProducto());
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error actualizando el producto con id: " + producto.getIdProducto(), excepcion);
        }
    }

    @Override
    public boolean eliminar(Integer idProducto) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {
            sentencia.setInt(1, idProducto);
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException excepcion) {
            throw new PersistenciaException(
                    "Error eliminando el producto con id: " + idProducto +
                            ". Verifique que no tenga variantes o movimientos asociados.", excepcion);
        }
    }

    @Override
    public Producto consultarPorId(Integer idProducto) {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_POR_ID)) {
            sentencia.setInt(1, idProducto);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearProducto(resultado);
                }
            }
            return null;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando el producto con id: " + idProducto, excepcion);
        }
    }

    @Override
    public List<Producto> consultarTodos() {
        List<Producto> productos = new ArrayList<>();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_TODOS);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                productos.add(mapearProducto(resultado));
            }
            return productos;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error consultando el listado de productos.", excepcion);
        }
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_NOMBRE)) {
            sentencia.setString(1, "%" + nombre + "%");
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    productos.add(mapearProducto(resultado));
                }
            }
            return productos;
        } catch (SQLException excepcion) {
            throw new PersistenciaException("Error buscando productos por nombre: " + nombre, excepcion);
        }
    }

    /**
     * Convierte la fila actual de un ResultSet en un objeto Producto,
     * incluyendo los nombres legibles de categoria y proveedor.
     */
    private Producto mapearProducto(ResultSet resultado) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(resultado.getInt("id_producto"));
        producto.setNombreProducto(resultado.getString("nombre_producto"));
        producto.setDescripcion(resultado.getString("descripcion"));
        producto.setMarca(resultado.getString("marca"));
        producto.setGenero(Genero.valueOf(resultado.getString("genero")));
        producto.setPrecioCompra(resultado.getBigDecimal("precio_compra"));
        producto.setPrecioVenta(resultado.getBigDecimal("precio_venta"));
        producto.setEstado(EstadoProducto.valueOf(resultado.getString("estado")));
        Timestamp fechaRegistro = resultado.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            producto.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }
        producto.setIdCategoria(resultado.getInt("id_categoria"));
        producto.setNombreCategoria(resultado.getString("nombre_categoria"));
        producto.setIdProveedor(resultado.getInt("id_proveedor"));
        producto.setNombreProveedor(resultado.getString("nombre_proveedor"));
        return producto;
    }
}
