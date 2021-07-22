package helloworld;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A basic helloworld servlet using injection.
 */
@SuppressWarnings("serial")
@WebServlet("/helloworld")
public class HelloWorldServlet extends HttpServlet {
    @Inject
    HelloService helloService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        resp.setContentType("text/html");
        var writer = resp.getWriter();

        printHeader(writer);

        writer.println("<h1>");
        writer.println(helloService.createGreeting("World"));
        writer.println("</h1>");

        printFooter(writer);
        writer.close();
    }

    private void printHeader(PrintWriter writer) {
        writer.println("<html><head><title>helloworld</title></head><body>");
    }

    private void printFooter(PrintWriter writer) {
        writer.println("</body></html>");
    }

}
