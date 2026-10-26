<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Stockly - Inventario</title>
</head>
<body>
    <h1>Stockly</h1>
    <h2>Sistema de inventario para tienda de ropa</h2>

    <p>Bienvenido al módulo web de Stockly.</p>

    <form action="productos" method="get">
        <button type="submit">Consultar productos</button>
    </form>

    <form action="productos" method="post">
        <label for="nombre">Nombre del producto:</label>
        <input type="text" id="nombre" name="nombre" required>

        <button type="submit">Registrar producto</button>
    </form>
</body>
</html>