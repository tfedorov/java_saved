/**
 * 
 */
package ua.com.make_bet.web;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ua.com.make_bet.bean.Matche;
import ua.com.make_bet.service.MatchService;


import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * @author tfedorov
 * 
 */
@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class BetsController {

	private static final String MATCHES_URI = "/matches";
  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();
	private static Log logger = LogFactory.getLog(BetsController.class);
	@Autowired
	private MatchService service;

	@Deprecated
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public @ResponseBody
	TempBean getObject() {
		EtmPoint perfPoint = getPerformancePoint("/hello");
		try {
			logger.info(String.format("Make call"));
			return new TempBean();
		} finally {
			perfPoint.collect();
		}
	}
	@Deprecated
	class TempBean{
		final private String changed;
		public TempBean() {
			changed = String.format("World number %s", Math.random());
		}
		public String getChanged() {
			return changed;
		}
	}
	   @RequestMapping(value = MATCHES_URI, method = RequestMethod.GET)
	    public @ResponseBody
	    List<Matche> getAvailableMatches() {
	        EtmPoint perfPoint = getPerformancePoint(MATCHES_URI);
	        try {
	          
	            logger.info(String.format("Make match call"));
	            return service.getAllMatches();
	        } finally {
	            perfPoint.collect();
	        }
	    }
	
	private EtmPoint getPerformancePoint(String name) {
		return performanceMonitor
				.createPoint(this.getClass().toString() + name);
	}
}
