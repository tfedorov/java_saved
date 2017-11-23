/**
 * 
 */
package com.tfedorov.social.web.servlet;


import com.tfedorov.social.utils.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author tfedorov
 * 
 */
public class DBMetadataServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5709654603240899618L;

	@Autowired
	private MetaDataDao metaDataDao;

	private Logger logger = LoggerFactory.getLogger(DBMetadataServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		logger.trace("Script printing servlet has been started.");

		response.setContentType("text/html");

		PrintWriter writer = response.getWriter();

		writer.print("<html><body>");

		if (req.getParameter("print_scripts") != null) {
			// printing list of launched dbscripts
			printScriptListTable(writer);
		} else if (req.getParameter("table_patterns") != null) {
			printTables(req.getParameter("table_patterns"), writer);
			// if no parameter in this case print all tables
		} else {
			printTables(null, writer);
		}

		writer.print("</body></html>");
		writer.close();

	}

	private void printTables(String pattern, PrintWriter writer) {
		try {
			List<String> listTables;
			if (pattern != null && !pattern.isEmpty()) {
				listTables = metaDataDao.getTableListByPattern(pattern);
			} else {
				listTables = metaDataDao.getAllTableList();
			}

			logger.trace("Succesfully reading tables " + listTables.size() + " rows.");

			writer.println("List of executed database scripts:<br/>");
			writer.println("<ol>");

			for (String table : listTables) {
				writer.println("<li>" + table + "</lis>");
			}

			writer.println("</ol>");
		} catch (MetaDataAccessException exs) {
			logger.error("Print tables exception", exs);
			writer.write("Print tables exception" + exs);
		}
	}

	private void printScriptListTable(PrintWriter writer) {
		List<ScriptBean> scriptsListFromDb = metaDataDao.getScriptsFromDb();
		logger.trace("Succesfully reading from scripts table. Detected " + scriptsListFromDb.size() + " rows.");

		writer.println("List of executed database scripts:<br/>");
		writer.println("<table border=\"1\"><thead><tr><td>Script name</td><td>Execution time</td></tr></thead>");

		for (ScriptBean script : scriptsListFromDb) {
			writer.println("<tr>");
			writer.println("<td>" + script.getScriptName() + "</td>");
			writer.println("<td>" + DateUtils.printDateTZ(DateUtils.convertToDateMidnight(script.getScriptDate())) + "</td>");
			writer.println("</tr>");
		}

		writer.println("</table>");
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		super.init(config);
		logger.info("[Init finished]");
	}

}
