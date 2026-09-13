package co.sena.stockly.dao;

import co.sena.stockly.modelo.Producto;

import java.util.List;

/**
 * Operaciones de acceso a datos disponibles para la entidad Producto.
 * Ademas del CRUD basico, expone una busqueda por nombre, ya que es
 * la consulta mas frecuente desde el modulo de inventario del
 * aplicativo Stockly (ver pantalla "Inventario" del prototipo).
 */
public interface ProductoDao extends DaoGenerico<Producto, Integer> {

    /**
     * Busca productos cuyo nombre contenga el texto indicado
     * (busqueda parcial, no sensible a mayusculas/minusculas).
     *
     * @param nombre texto a buscar dentro del nombre del producto
     * @return lista de productos que coinciden con el criterio
     */
    List<Producto> buscarPorNombre(String nombre);
}
