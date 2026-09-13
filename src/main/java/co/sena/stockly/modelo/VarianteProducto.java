package co.sena.stockly.modelo;

/**
 * Representa una combinacion especifica de producto, talla y color,
 * identificada de forma unica por un SKU y un codigo QR, junto con
 * su stock actual y minimo. Corresponde a la tabla "variantes_producto"
 * del modelo relacional.
 */
public class VarianteProducto {

    private int idVariante;
    private int idProducto;
    private String nombreProducto;
    private int idTalla;
    private String nombreTalla;
    private int idColor;
    private String nombreColor;
    private String sku;
    private String codigoQr;
    private int stockActual;
    private int stockMinimo;

    public VarianteProducto() {
    }

    public VarianteProducto(int idProducto, int idTalla, int idColor, String sku,
                             String codigoQr, int stockActual, int stockMinimo) {
        this.idProducto = idProducto;
        this.idTalla = idTalla;
        this.idColor = idColor;
        this.sku = sku;
        this.codigoQr = codigoQr;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }

    public int getIdVariante() {
        return idVariante;
    }

    public void setIdVariante(int idVariante) {
        this.idVariante = idVariante;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getIdTalla() {
        return idTalla;
    }

    public void setIdTalla(int idTalla) {
        this.idTalla = idTalla;
    }

    public String getNombreTalla() {
        return nombreTalla;
    }

    public void setNombreTalla(String nombreTalla) {
        this.nombreTalla = nombreTalla;
    }

    public int getIdColor() {
        return idColor;
    }

    public void setIdColor(int idColor) {
        this.idColor = idColor;
    }

    public String getNombreColor() {
        return nombreColor;
    }

    public void setNombreColor(String nombreColor) {
        this.nombreColor = nombreColor;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    @Override
    public String toString() {
        return "VarianteProducto{" +
                "idVariante=" + idVariante +
                ", producto='" + nombreProducto + '\'' +
                ", talla='" + nombreTalla + '\'' +
                ", color='" + nombreColor + '\'' +
                ", sku='" + sku + '\'' +
                ", stockActual=" + stockActual +
                ", stockMinimo=" + stockMinimo +
                '}';
    }
}
