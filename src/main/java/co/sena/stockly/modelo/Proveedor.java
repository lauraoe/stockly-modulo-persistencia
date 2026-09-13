package co.sena.stockly.modelo;

/**
 * Representa un proveedor de mercancia para la tienda de ropa.
 * Corresponde a la tabla "proveedores" del modelo relacional.
 */
public class Proveedor {

    private int idProveedor;
    private String nombreProveedor;
    private String nit;
    private String telefono;
    private String correo;
    private String direccion;

    public Proveedor() {
    }

    public Proveedor(String nombreProveedor, String nit, String telefono, String correo, String direccion) {
        this.nombreProveedor = nombreProveedor;
        this.nit = nit;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    public Proveedor(int idProveedor, String nombreProveedor, String nit, String telefono,
                      String correo, String direccion) {
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.nit = nit;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
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

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "idProveedor=" + idProveedor +
                ", nombreProveedor='" + nombreProveedor + '\'' +
                ", nit='" + nit + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", direccion='" + direccion + '\'' +
                '}';
    }
}
