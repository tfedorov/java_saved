package com.tfedorov.performance.memoryinspect;
/*
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;
*/

/**
 * @author tfedorov
 */
public class MetadataServlet {
}/*extends HttpServlet {


    private static final long serialVersionUID = -1108754338296919571L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter writer = resp.getWriter();

        writer.print("<html><body>");
        writer.print("Current user session map:<ol>");

        String formatedMessage = MemoryPrinterUtil.memoryInHtml();
        writer.write("<h4>[Memmory usage: free(%)/total/max = "
                + formatedMessage
                + "]</h4>");
        writer.print("</body></html>");
        writer.close();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
        super.init(config);
    }

}
*/