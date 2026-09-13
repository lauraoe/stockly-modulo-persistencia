package co.sena.stockly.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un producto del catalogo de Stockly (por ejemplo, una
 * referencia de camiseta o pantalon), sin incluir aun la combinacion
 * especifica de talla/color (eso corresponde a VarianteProducto).
 * Corresponde a la tabla "productos" del modelo relacional.
 */
public class Producto {

    private int idProducto;
    private String nombreProducto;
    private String descripcion;
    private String marca;
    private Genero genero;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private int idCategoria;
    private String nombreCategoria;
    private int idProveedor;
    private String nombreProveedor;
    private EstadoProducto estado;
    private LocalDateTime fechaRegistro;

    public Producto() {
    }

    public Producto(String nombreProducto, String descripcion, String marca, Genero genero,
                     BigDecimal precioCompra, BigDecimal precioVenta, int idCategoria, int idProveedor) {
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.marca = marca;
        this.genero = genero;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        this.estado = EstadoProducto.ACTIVO;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", marca='" + marca + '\'' +
                ", genero=" + genero +
                ", precioCompra=" + precioCompra +
                ", precioVenta=" + precioVenta +
                ", categoria='" + nombreCategoria + '\'' +
                ", proveedor='" + nombreProveedor + '\'' +
                ", estado=" + estado +
                '}';
    }
}
