package com.tfedorov.social.twitter.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tfedorov.social.twitter.streaming.TwitterStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.tfedorov.social.twitter.emulation.TwitterStreamingEmulationService;

public class TweetsEmulatingServlet extends HttpServlet {

	@Autowired
	private TwitterStreamingService twitterStreamingService;

	@Autowired
	private TwitterStreamingEmulationService twitterStreamingEmulationService;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5921679788857899521L;


	private Logger logger = LoggerFactory.getLogger(TweetsEmulatingServlet.class);

	public TweetsEmulatingServlet() {

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
		super.init(config);
		logger.info("[Init finished]");
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");

		PrintWriter writer = response.getWriter();
		writer.write("<h2>[ Twitter Streaming Emulate Servlet ]</h2>");

		if (request.getParameter("send") != null) {
			int tweetsLimit = 0;
			tweetsLimit = Integer.parseInt(request.getParameter("send"));

			if (twitterStreamingService.isActive()) {
				writer.write("<h3>Please stop Twitter Streaming Service before!!!</h3>");
			} else {
				int processed = twitterStreamingEmulationService.sendTweets(tweetsLimit);
				writer.write("<h3>"+ processed+" tweets processed </h3>");

			}
		} 
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String tweet = request.getParameter("tweet");
		PrintWriter writer = response.getWriter();
		if (tweet != null && !tweet.isEmpty())
		{


			if (twitterStreamingService.isActive())
			{
				writer.write("<h3>Please stop Twitter Streaming Service before!!!1</h3>");
			}
			else
			{
				String serviceResponce = twitterStreamingEmulationService.sendTweet(tweet);
				writer.write(serviceResponce);
			}
		}
		else
		{
			writer.write("Input is empty. You need to send in x-www-form-urlencoded");
		}
	}
}
