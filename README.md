# Stockly - Módulo de Persistencia JDBC

Módulo de **codificación** del proyecto **Stockly** (sistema de gestión de
inventarios para una tienda de ropa), correspondiente a la evidencia de
codificación del módulo del proyecto con conexión a base de datos por
medio de **JDBC**.

Este módulo es la continuación natural del trabajo entregado en las
evidencias anteriores del proyecto formativo:

| Artefacto previo | Cómo se usó en este módulo |
|---|---|
| Modelo relacional de base de datos (GA6-220501096-AA1-EV01) y script BD (GA6-220501096-AA2-EV03) | Es la base exacta de las tablas, llaves y restricciones contra las que se conecta el módulo (carpeta `sql/`). |
| Diagrama de clases del proyecto (GA4-220501095-AA2-EV04) | Define las clases de modelo (`Producto`, `Categoria`, `Proveedor`, `VarianteProducto`) y sus atributos. |
| Historias de usuario / prototipo de interfaz (GA6-220501096-AA3-EV03, `front.zip`) | Define las funcionalidades que el módulo debe soportar: alta de producto, listado/inventario, edición, eliminación, categorías, control de stock. |
| Informe técnico / propuesta técnica (GA2-220501094-AA3-EV02) | Sustenta la tecnología seleccionada: Java + JDBC + MySQL. |

## Tecnologías utilizadas

- **Java 17**
- **JDBC** (paquete `java.sql`) con **MySQL Connector/J 8.4.0**
- **Maven** como gestor de dependencias y construcción del proyecto
- **MySQL 8.0** como motor de base de datos (`stockly_db`)
- **Git** como herramienta de versionamiento

## Estructura del proyecto

```
stockly-modulo-persistencia/
├── pom.xml
├── sql/
│   ├── 01_stockly_modelo_fisico.sql   -> script para crear la BD (tablas, triggers, vistas)
│   └── 02_stockly_datos_prueba.sql    -> datos de prueba opcionales
├── src/main/resources/
│   └── config.properties              -> datos de conexión (url, usuario, clave, driver)
└── src/main/java/co/sena/stockly/
    ├── conexion/
    │   └── ConexionBD.java            -> clase Singleton que abre/entrega la conexión JDBC
    ├── modelo/
    │   ├── Categoria.java
    │   ├── Proveedor.java
    │   ├── Producto.java
    │   ├── VarianteProducto.java
    │   ├── Genero.java
    │   └── EstadoProducto.java
    ├── dao/
    │   ├── DaoGenerico.java           -> contrato CRUD genérico
    │   ├── CategoriaDao.java
    │   ├── ProveedorDao.java
    │   ├── ProductoDao.java
    │   ├── VarianteProductoDao.java
    │   └── impl/                      -> implementaciones JDBC (PreparedStatement) de cada DAO
    ├── excepcion/
    │   └── PersistenciaException.java -> excepción propia para errores de acceso a datos
    └── app/
        └── StocklyConsolaApp.java     -> módulo ejecutable con menú de consola para probar el CRUD
```

## Estándares de codificación aplicados

- **Paquetes:** en minúsculas y con dominio inverso, `co.sena.stockly.<capa>` (ej. `co.sena.stockly.dao.impl`).
- **Clases e interfaces:** PascalCase y sustantivos descriptivos (`ProductoDaoImpl`, `ConexionBD`, `PersistenciaException`).
- **Métodos:** camelCase, verbo + sustantivo (`insertar`, `consultarPorId`, `actualizar`, `eliminar`, `buscarPorNombre`).
- **Variables:** camelCase y con nombre significativo en español, evitando abreviaturas (`idProducto`, `precioVenta`, `filasAfectadas`).
- **Constantes:** UPPER_SNAKE_CASE (`SQL_INSERTAR`, `ARCHIVO_CONFIGURACION`).
- Separación por capas (modelo / conexión / DAO / aplicación) y uso de interfaces para desacoplar el contrato CRUD de su implementación JDBC.
- Uso de `PreparedStatement` en todas las consultas para evitar inyección SQL.
- Manejo de errores centralizado con una excepción propia (`PersistenciaException`) en lugar de propagar `SQLException` a capas superiores.

## Funcionalidades CRUD incluidas

Cada una de las siguientes entidades cuenta con **inserción, consulta (por id y listado completo), actualización y eliminación**:

- **Categorías** (`categorias`)
- **Proveedores** (`proveedores`)
- **Productos** (`productos`), además de búsqueda por nombre
- **Variantes de producto** (`variantes_producto`: talla + color + SKU + stock), además de consulta por código QR y consulta de stock bajo

> El campo `stock_actual` de las variantes se actualiza automáticamente
> mediante los *triggers* `trg_entradas_after_insert` y
> `trg_salidas_after_insert` definidos en el script de base de datos,
> replicando la regla de negocio ya validada en la evidencia de script BD.

## Cómo ejecutar el módulo

1. **Crear la base de datos.** En MySQL Workbench o la terminal de MySQL, ejecutar:
   ```
   sql/01_stockly_modelo_fisico.sql
   ```
   Opcionalmente, cargar datos de prueba con:
   ```
   sql/02_stockly_datos_prueba.sql
   ```

2. **Configurar la conexión.** Editar `src/main/resources/config.properties`
   con el usuario y la clave de tu servidor MySQL local:
   ```
   db.url=jdbc:mysql://localhost:3306/stockly_db?useSSL=false&serverTimezone=America/Bogota&characterEncoding=UTF-8
   db.usuario=root
   db.clave=root
   ```

3. **Compilar y ejecutar con Maven:**
   ```
   mvn clean package
   java -jar target/stockly-modulo-persistencia-jar-with-dependencies.jar
   ```

4. Usar el menú de consola para insertar, consultar, actualizar y
   eliminar registros de categorías, proveedores, productos y
   variantes de producto.

## Control de versiones

El proyecto se inicializó con Git (`git init`) y su historial de commits
queda incluido en la carpeta `.git/` del comprimido. El enlace del
repositorio remoto (GitHub) se encuentra en el archivo
`ENLACE_REPOSITORIO.txt`, en la raíz de la entrega.
