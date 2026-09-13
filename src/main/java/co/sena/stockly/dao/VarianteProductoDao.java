package co.sena.stockly.dao;

import co.sena.stockly.modelo.VarianteProducto;

import java.util.List;

/**
 * Operaciones de acceso a datos disponibles para la entidad
 * VarianteProducto (combinacion producto + talla + color).
 */
public interface VarianteProductoDao extends DaoGenerico<VarianteProducto, Integer> {

    /**
     * Consulta una variante de producto a partir de su codigo QR,
     * tal como lo requiere la funcionalidad de escaneo del
     * aplicativo Stockly.
     *
     * @param codigoQr codigo QR unico de la variante
     * @return la variante encontrada, o null si no existe
     */
    VarianteProducto consultarPorCodigoQr(String codigoQr);

    /**
     * Consulta las variantes cuyo stock actual esta en o por debajo
     * del stock minimo definido (alerta de stock bajo).
     *
     * @return lista de variantes con stock bajo
     */
    List<VarianteProducto> consultarStockBajo();
}
