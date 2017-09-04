/**
 * 
 */
package ua.com.make_bet.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * @author tfedorov
 * 
 */
public class MetadataServlet extends HttpServlet {

	
	private static final int MEMORY_SIZE = 1024;

	private static final long serialVersionUID = -1108754338296919571L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		PrintWriter writer = resp.getWriter();

		writer.print("<html><body>");
		writer.print("Current user session map:<ol>");

		 String formatedMessage = printMemmory();
			writer.write("<h4>[Memmory usage: free(%)/total/max = "
		        + formatedMessage
		        + "]</h4>");
		writer.print("</body></html>");
		writer.close();
	}

	private String printMemmory() {
		long mbFree = Runtime.getRuntime().freeMemory() / (MEMORY_SIZE * MEMORY_SIZE);
	    long mbTotal = Runtime.getRuntime().totalMemory() / (MEMORY_SIZE * MEMORY_SIZE);
	    long mbMax = Runtime.getRuntime().maxMemory() / (MEMORY_SIZE * MEMORY_SIZE);
	    double pFree = (double) mbFree / (double) mbTotal;
	    
	    Object[] mArgFM = {pFree * 100};
	    String freePerc = MessageFormat.format("({0,number,integer}%)", mArgFM);
	    if (pFree < 0.2) {
	      freePerc =
	          MessageFormat.format("<font color=\"red\"> ({0,number,integer}% !!!) </font>", mArgFM);
	    } else if (pFree < 0.3) {
	      freePerc =
	          MessageFormat.format("<font color=\"orange\"> ({0,number,integer}% !!!) </font>", mArgFM);
	    }

	    Object[] mArg = {mbFree, freePerc, mbTotal, mbMax};
	    String formatedMessage = MessageFormat.format(
		    "{0,number,integer}Mb {1} / {2,number,integer}Mb / {3,number,integer}Mb", mArg);
		return formatedMessage;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		super.init(config);
	}
}
