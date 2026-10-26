package co.sena.stockly.servlet;



import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import co.sena.stockly.dao.impl.ProductoDaoImpl;
import co.sena.stockly.modelo.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import co.sena.stockly.dao.impl.ProductoDaoImpl;
import co.sena.stockly.modelo.Producto;
import java.util.List;
@WebServlet("/productos")
public class ProductoServlet extends HttpServlet {

@Override
protected void doGet(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("text/html;charset=UTF-8");

    ProductoDaoImpl productoDao = new ProductoDaoImpl();
    List<Producto> productos = productoDao.consultarTodos();

    PrintWriter out = response.getWriter();

    out.println("<!DOCTYPE html>");
    out.println("<html lang='es'>");
    out.println("<head><meta charset='UTF-8'>");
    out.println("<title>Stockly - Productos</title></head>");
    out.println("<body>");
    out.println("<h1>Productos de Stockly</h1>");
    out.println("<p>Consulta de productos registrados.</p>");
    out.println("<a href='index.jsp'>Volver al inicio</a>");
    out.println("</body></html>");
}

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nombre = request.getParameter("nombre");

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head><meta charset='UTF-8'><title>Registro - Stockly</title></head>");
        out.println("<body>");
        out.println("<h1>Stockly - Registro de producto</h1>");

        if (nombre != null && !nombre.trim().isEmpty()) {
            out.println("<p>Producto recibido: "
                    + escapeHtml(nombre.trim()) + "</p>");
        } else {
            out.println("<p>Debes escribir el nombre del producto.</p>");
        }

        out.println("<a href='index.jsp'>Volver al inicio</a>");
        out.println("</body></html>");
    }

    private String escapeHtml(String texto) {
        return texto.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
 
    
}
