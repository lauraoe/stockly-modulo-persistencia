package co.sena.stockly.app;

import co.sena.stockly.conexion.ConexionBD;
import co.sena.stockly.dao.CategoriaDao;
import co.sena.stockly.dao.ProductoDao;
import co.sena.stockly.dao.ProveedorDao;
import co.sena.stockly.dao.VarianteProductoDao;
import co.sena.stockly.dao.impl.CategoriaDaoImpl;
import co.sena.stockly.dao.impl.ProductoDaoImpl;
import co.sena.stockly.dao.impl.ProveedorDaoImpl;
import co.sena.stockly.dao.impl.VarianteProductoDaoImpl;
import co.sena.stockly.excepcion.PersistenciaException;
import co.sena.stockly.modelo.Categoria;
import co.sena.stockly.modelo.EstadoProducto;
import co.sena.stockly.modelo.Genero;
import co.sena.stockly.modelo.Producto;
import co.sena.stockly.modelo.Proveedor;
import co.sena.stockly.modelo.VarianteProducto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Modulo ejecutable del proyecto Stockly. Presenta un menu de consola
 * que permite probar, contra la base de datos real (MySQL), las
 * operaciones de insercion, consulta, actualizacion y eliminacion
 * (CRUD) para las entidades principales del inventario: categorias,
 * proveedores, productos y variantes de producto.
 *
 * Este modulo corresponde a la capa de logica/presentacion que
 * consume el modulo de persistencia (paquetes conexion, modelo y
 * dao) construido con JDBC.
 */
public class StocklyConsolaApp {

    private static final Scanner ENTRADA = new Scanner(System.in);

    private static final CategoriaDao CATEGORIA_DAO = new CategoriaDaoImpl();
    private static final ProveedorDao PROVEEDOR_DAO = new ProveedorDaoImpl();
    private static final ProductoDao PRODUCTO_DAO = new ProductoDaoImpl();
    private static final VarianteProductoDao VARIANTE_DAO = new VarianteProductoDaoImpl();

    public static void main(String[] argumentos) {
        mostrarEncabezado();
        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerOpcionEntera();
            try {
                switch (opcion) {
                    case 1 -> gestionarCategorias();
                    case 2 -> gestionarProveedores();
                    case 3 -> gestionarProductos();
                    case 4 -> gestionarVariantes();
                    case 0 -> continuar = false;
                    default -> System.out.println("Opcion invalida. Intente de nuevo.");
                }
            } catch (PersistenciaException excepcionPersistencia) {
                System.out.println("Ocurrio un error accediendo a la base de datos: "
                        + excepcionPersistencia.getMessage());
            }
        }
        ConexionBD.obtenerInstancia().cerrarConexion();
        System.out.println("Conexion cerrada. Hasta pronto.");
    }

    private static void mostrarEncabezado() {
        System.out.println("=====================================================");
        System.out.println(" STOCKLY - Modulo de codificacion con conexion JDBC ");
        System.out.println("=====================================================");
    }

    private static void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("Seleccione el modulo a gestionar:");
        System.out.println("1. Categorias");
        System.out.println("2. Proveedores");
        System.out.println("3. Productos");
        System.out.println("4. Variantes de producto (talla / color / stock)");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
    }

    // ---------------------------------------------------------------
    // MODULO: CATEGORIAS
    // ---------------------------------------------------------------
    private static void gestionarCategorias() {
        boolean continuar = true;
        while (continuar) {
            System.out.println();
            System.out.println("--- Gestion de categorias ---");
            System.out.println("1. Insertar categoria");
            System.out.println("2. Consultar todas las categorias");
            System.out.println("3. Actualizar categoria");
            System.out.println("4. Eliminar categoria");
            System.out.println("0. Volver al menu principal");
            System.out.print("Opcion: ");
            int opcion = leerOpcionEntera();
            switch (opcion) {
                case 1 -> insertarCategoria();
                case 2 -> consultarCategorias();
                case 3 -> actualizarCategoria();
                case 4 -> eliminarCategoria();
                case 0 -> continuar = false;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void insertarCategoria() {
        System.out.print("Nombre de la categoria: ");
        String nombre = ENTRADA.nextLine();
        System.out.print("Descripcion: ");
        String descripcion = ENTRADA.nextLine();

        Categoria categoria = new Categoria(nombre, descripcion);
        Integer idGenerado = CATEGORIA_DAO.insertar(categoria);
        System.out.println("Categoria insertada correctamente con id: " + idGenerado);
    }

    private static void consultarCategorias() {
        List<Categoria> categorias = CATEGORIA_DAO.consultarTodos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias registradas.");
            return;
        }
        categorias.forEach(System.out::println);
    }

    private static void actualizarCategoria() {
        System.out.print("Id de la categoria a actualizar: ");
        int id = leerOpcionEntera();
        Categoria categoria = CATEGORIA_DAO.consultarPorId(id);
        if (categoria == null) {
            System.out.println("No existe una categoria con ese id.");
            return;
        }
        System.out.print("Nuevo nombre (" + categoria.getNombreCategoria() + "): ");
        categoria.setNombreCategoria(ENTRADA.nextLine());
        System.out.print("Nueva descripcion (" + categoria.getDescripcion() + "): ");
        categoria.setDescripcion(ENTRADA.nextLine());

        boolean actualizado = CATEGORIA_DAO.actualizar(categoria);
        System.out.println(actualizado ? "Categoria actualizada correctamente." : "No se pudo actualizar la categoria.");
    }

    private static void eliminarCategoria() {
        System.out.print("Id de la categoria a eliminar: ");
        int id = leerOpcionEntera();
        boolean eliminado = CATEGORIA_DAO.eliminar(id);
        System.out.println(eliminado ? "Categoria eliminada correctamente." : "No se pudo eliminar la categoria.");
    }

    // ---------------------------------------------------------------
    // MODULO: PROVEEDORES
    // ---------------------------------------------------------------
    private static void gestionarProveedores() {
        boolean continuar = true;
        while (continuar) {
            System.out.println();
            System.out.println("--- Gestion de proveedores ---");
            System.out.println("1. Insertar proveedor");
            System.out.println("2. Consultar todos los proveedores");
            System.out.println("3. Actualizar proveedor");
            System.out.println("4. Eliminar proveedor");
            System.out.println("0. Volver al menu principal");
            System.out.print("Opcion: ");
            int opcion = leerOpcionEntera();
            switch (opcion) {
                case 1 -> insertarProveedor();
                case 2 -> consultarProveedores();
                case 3 -> actualizarProveedor();
                case 4 -> eliminarProveedor();
                case 0 -> continuar = false;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void insertarProveedor() {
        System.out.print("Nombre del proveedor: ");
        String nombre = ENTRADA.nextLine();
        System.out.print("NIT: ");
        String nit = ENTRADA.nextLine();
        System.out.print("Telefono: ");
        String telefono = ENTRADA.nextLine();
        System.out.print("Correo: ");
        String correo = ENTRADA.nextLine();
        System.out.print("Direccion: ");
        String direccion = ENTRADA.nextLine();

        Proveedor proveedor = new Proveedor(nombre, nit, telefono, correo, direccion);
        Integer idGenerado = PROVEEDOR_DAO.insertar(proveedor);
        System.out.println("Proveedor insertado correctamente con id: " + idGenerado);
    }

    private static void consultarProveedores() {
        List<Proveedor> proveedores = PROVEEDOR_DAO.consultarTodos();
        if (proveedores.isEmpty()) {
            System.out.println("No hay proveedores registrados.");
            return;
        }
        proveedores.forEach(System.out::println);
    }

    private static void actualizarProveedor() {
        System.out.print("Id del proveedor a actualizar: ");
        int id = leerOpcionEntera();
        Proveedor proveedor = PROVEEDOR_DAO.consultarPorId(id);
        if (proveedor == null) {
            System.out.println("No existe un proveedor con ese id.");
            return;
        }
        System.out.print("Nuevo telefono (" + proveedor.getTelefono() + "): ");
        proveedor.setTelefono(ENTRADA.nextLine());
        System.out.print("Nuevo correo (" + proveedor.getCorreo() + "): ");
        proveedor.setCorreo(ENTRADA.nextLine());
        System.out.print("Nueva direccion (" + proveedor.getDireccion() + "): ");
        proveedor.setDireccion(ENTRADA.nextLine());

        boolean actualizado = PROVEEDOR_DAO.actualizar(proveedor);
        System.out.println(actualizado ? "Proveedor actualizado correctamente." : "No se pudo actualizar el proveedor.");
    }

    private static void eliminarProveedor() {
        System.out.print("Id del proveedor a eliminar: ");
        int id = leerOpcionEntera();
        boolean eliminado = PROVEEDOR_DAO.eliminar(id);
        System.out.println(eliminado ? "Proveedor eliminado correctamente." : "No se pudo eliminar el proveedor.");
    }

    // ---------------------------------------------------------------
    // MODULO: PRODUCTOS
    // ---------------------------------------------------------------
    private static void gestionarProductos() {
        boolean continuar = true;
        while (continuar) {
            System.out.println();
            System.out.println("--- Gestion de productos ---");
            System.out.println("1. Insertar producto");
            System.out.println("2. Consultar todos los productos");
            System.out.println("3. Buscar productos por nombre");
            System.out.println("4. Actualizar producto");
            System.out.println("5. Eliminar producto");
            System.out.println("0. Volver al menu principal");
            System.out.print("Opcion: ");
            int opcion = leerOpcionEntera();
            switch (opcion) {
                case 1 -> insertarProducto();
                case 2 -> consultarProductos();
                case 3 -> buscarProductosPorNombre();
                case 4 -> actualizarProducto();
                case 5 -> eliminarProducto();
                case 0 -> continuar = false;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void insertarProducto() {
        System.out.print("Nombre del producto: ");
        String nombre = ENTRADA.nextLine();
        System.out.print("Descripcion: ");
        String descripcion = ENTRADA.nextLine();
        System.out.print("Marca: ");
        String marca = ENTRADA.nextLine();
        System.out.print("Genero (HOMBRE, MUJER, UNISEX, NINO, NINA): ");
        Genero genero = Genero.valueOf(ENTRADA.nextLine().trim().toUpperCase());
        System.out.print("Precio de compra: ");
        BigDecimal precioCompra = new BigDecimal(ENTRADA.nextLine());
        System.out.print("Precio de venta: ");
        BigDecimal precioVenta = new BigDecimal(ENTRADA.nextLine());
        System.out.print("Id de la categoria: ");
        int idCategoria = leerOpcionEntera();
        System.out.print("Id del proveedor: ");
        int idProveedor = leerOpcionEntera();

        Producto producto = new Producto(nombre, descripcion, marca, genero, precioCompra,
                precioVenta, idCategoria, idProveedor);
        Integer idGenerado = PRODUCTO_DAO.insertar(producto);
        System.out.println("Producto insertado correctamente con id: " + idGenerado);
    }

    private static void consultarProductos() {
        List<Producto> productos = PRODUCTO_DAO.consultarTodos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        productos.forEach(System.out::println);
    }

    private static void buscarProductosPorNombre() {
        System.out.print("Texto a buscar en el nombre del producto: ");
        String texto = ENTRADA.nextLine();
        List<Producto> productos = PRODUCTO_DAO.buscarPorNombre(texto);
        if (productos.isEmpty()) {
            System.out.println("No se encontraron productos que coincidan con la busqueda.");
            return;
        }
        productos.forEach(System.out::println);
    }

    private static void actualizarProducto() {
        System.out.print("Id del producto a actualizar: ");
        int id = leerOpcionEntera();
        Producto producto = PRODUCTO_DAO.consultarPorId(id);
        if (producto == null) {
            System.out.println("No existe un producto con ese id.");
            return;
        }
        System.out.print("Nuevo precio de venta (" + producto.getPrecioVenta() + "): ");
        producto.setPrecioVenta(new BigDecimal(ENTRADA.nextLine()));
        System.out.print("Nuevo estado (ACTIVO, DESCONTINUADO) [" + producto.getEstado() + "]: ");
        producto.setEstado(EstadoProducto.valueOf(ENTRADA.nextLine().trim().toUpperCase()));

        boolean actualizado = PRODUCTO_DAO.actualizar(producto);
        System.out.println(actualizado ? "Producto actualizado correctamente." : "No se pudo actualizar el producto.");
    }

    private static void eliminarProducto() {
        System.out.print("Id del producto a eliminar: ");
        int id = leerOpcionEntera();
        boolean eliminado = PRODUCTO_DAO.eliminar(id);
        System.out.println(eliminado ? "Producto eliminado correctamente." : "No se pudo eliminar el producto.");
    }

    // ---------------------------------------------------------------
    // MODULO: VARIANTES DE PRODUCTO
    // ---------------------------------------------------------------
    private static void gestionarVariantes() {
        boolean continuar = true;
        while (continuar) {
            System.out.println();
            System.out.println("--- Gestion de variantes de producto ---");
            System.out.println("1. Insertar variante");
            System.out.println("2. Consultar todas las variantes");
            System.out.println("3. Consultar variante por codigo QR");
            System.out.println("4. Consultar variantes con stock bajo");
            System.out.println("5. Actualizar variante");
            System.out.println("6. Eliminar variante");
            System.out.println("0. Volver al menu principal");
            System.out.print("Opcion: ");
            int opcion = leerOpcionEntera();
            switch (opcion) {
                case 1 -> insertarVariante();
                case 2 -> consultarVariantes();
                case 3 -> consultarVariantePorQr();
                case 4 -> consultarStockBajo();
                case 5 -> actualizarVariante();
                case 6 -> eliminarVariante();
                case 0 -> continuar = false;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void insertarVariante() {
        System.out.print("Id del producto: ");
        int idProducto = leerOpcionEntera();
        System.out.print("Id de la talla: ");
        int idTalla = leerOpcionEntera();
        System.out.print("Id del color: ");
        int idColor = leerOpcionEntera();
        System.out.print("SKU: ");
        String sku = ENTRADA.nextLine();
        System.out.print("Codigo QR: ");
        String codigoQr = ENTRADA.nextLine();
        System.out.print("Stock inicial: ");
        int stockActual = leerOpcionEntera();
        System.out.print("Stock minimo: ");
        int stockMinimo = leerOpcionEntera();

        VarianteProducto variante = new VarianteProducto(idProducto, idTalla, idColor, sku,
                codigoQr, stockActual, stockMinimo);
        Integer idGenerado = VARIANTE_DAO.insertar(variante);
        System.out.println("Variante insertada correctamente con id: " + idGenerado);
    }

    private static void consultarVariantes() {
        List<VarianteProducto> variantes = VARIANTE_DAO.consultarTodos();
        if (variantes.isEmpty()) {
            System.out.println("No hay variantes registradas.");
            return;
        }
        variantes.forEach(System.out::println);
    }

    private static void consultarVariantePorQr() {
        System.out.print("Codigo QR: ");
        String codigoQr = ENTRADA.nextLine();
        VarianteProducto variante = VARIANTE_DAO.consultarPorCodigoQr(codigoQr);
        System.out.println(variante != null ? variante : "No se encontro una variante con ese codigo QR.");
    }

    private static void consultarStockBajo() {
        List<VarianteProducto> variantes = VARIANTE_DAO.consultarStockBajo();
        if (variantes.isEmpty()) {
            System.out.println("No hay variantes con stock bajo.");
            return;
        }
        variantes.forEach(System.out::println);
    }

    private static void actualizarVariante() {
        System.out.print("Id de la variante a actualizar: ");
        int id = leerOpcionEntera();
        VarianteProducto variante = VARIANTE_DAO.consultarPorId(id);
        if (variante == null) {
            System.out.println("No existe una variante con ese id.");
            return;
        }
        System.out.print("Nuevo stock minimo (" + variante.getStockMinimo() + "): ");
        variante.setStockMinimo(leerOpcionEntera());

        boolean actualizado = VARIANTE_DAO.actualizar(variante);
        System.out.println(actualizado ? "Variante actualizada correctamente." : "No se pudo actualizar la variante.");
    }

    private static void eliminarVariante() {
        System.out.print("Id de la variante a eliminar: ");
        int id = leerOpcionEntera();
        boolean eliminado = VARIANTE_DAO.eliminar(id);
        System.out.println(eliminado ? "Variante eliminada correctamente." : "No se pudo eliminar la variante.");
    }

    // ---------------------------------------------------------------
    // UTILIDAD DE LECTURA
    // ---------------------------------------------------------------
    private static int leerOpcionEntera() {
        while (!ENTRADA.hasNextInt()) {
            System.out.print("Ingrese un valor numerico valido: ");
            ENTRADA.next();
        }
        int valor = ENTRADA.nextInt();
        ENTRADA.nextLine();
        return valor;
    }
}
