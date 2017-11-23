package com.tfedorov.social.twitter.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tfedorov.social.twitter.aggregation.TermsAggregationService;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate;
import com.tfedorov.social.utils.date.DateUtils;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class AggregationServlet extends HttpServlet {

  private static final String TOPIC = "topic";

  private static final String AGGREGATE = "aggregate";

  private static final String FUNCTION_DISABLED_MSG = "<h4>[ Function disabled !!!] </h4>";

  private static final String TERM_AGREGATION_OUT_MSG =
      "<h4> Term agregation is in progress - started by other thread!!!.</h4>";

  private static final long serialVersionUID = -5921679788857899521L;

  private Logger logger = LoggerFactory.getLogger(AggregationServlet.class);

  @Autowired
  private TermsAggregationService termService;

  public AggregationServlet() {}

  @Override
  public void init(ServletConfig config) throws ServletException {
    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
        config.getServletContext());
    super.init(config);
    logger.info("[Init finished]");
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("text/html");

    PrintWriter writer = response.getWriter();
    writer.write("<h2>[ Twitter Aggregation Servlet ]</h2>");

    if (request.getParameter(AGGREGATE) != null && request.getParameter(AGGREGATE).equals("today")) {
      writer.write("<h4>[ Starting terms preaggregation for today !!!] </h4>");
      if (termService.isTermAgregationRunning()) {
        writer.write(TERM_AGREGATION_OUT_MSG);
        return;
      }
      termService.aggregateAllTopicsTermsForToday();
      writer.write("<h4>[ Preaggregation done for all topics for today !!!] </h4>");
    } else if (request.getParameter(AGGREGATE) != null
        && request.getParameter(AGGREGATE).equals("all-30-days")) {
      writer.write("<h4>[ Preaggregation started for lats 30 days !!!] </h4>");
      // termService.aggregateAllTopicTermsFor30Days();
      // writer.write("<h4>[ Preaggregation done for all topics for last 30 days !!!] </h4>");
      writer.write(FUNCTION_DISABLED_MSG);
    } else if (request.getParameter(AGGREGATE) != null
        && request.getParameter(AGGREGATE).equals("part") && request.getParameter(TOPIC) != null) {


      DateMidnight date = DateUtils.getCurrentMidnight();

      String dateStr = request.getParameter("date");

      if (dateStr != null) {
        date = DateUtils.parseToMidnightDefTZ(dateStr, DateUtils.YYYY_MM_DD_FORMAT);
      }


      String periodStr = request.getParameter("period");
      String typeStr = request.getParameter("type");

      if (periodStr != null && typeStr != null) {


        // Integer period = Integer.parseInt(periodStr);

        PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type =
            PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p;

        if (typeStr.equals("terms")) {
          type = PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p;
        } else if (typeStr.equals("bi_terms")) {
          type = PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_bi_terms_p;
        } else if (typeStr.equals("tri_terms")) {
          type = PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_tri_terms_p;
        }

        // Used for debugger purposes only
        // if(termService.isTermAgregationLaunching()){
        // writer.write(TERM_AGREGATION_OUT_MSG);
        // return;
        // }

        // int result = termService.aggregateTermsPart(topicId, date, period, type);
        // writer.write("<h4>[ Preaggregation done - "+ result+" reccords preaggregated for topic: "
        // +topicId +", date: " +date+ ", period: " + period +", type: "+ type.name()+" ]</h4>");

        writer.write(FUNCTION_DISABLED_MSG);
      } else {

        // Used for debugger purposes only
        // if(termService.isTermAgregationLaunching()){
        // writer.write(TERM_AGREGATION_OUT_MSG);
        // return;
        // }

        // int result = termService.aggregateTermsForTopicDate(topicId, date);
        // writer.write("<h4>[ Preaggregation done - "+ result+" reccords preaggregated for topic: "
        // +topicId +", date: " +date+ " ]</h4>");

        writer.write(FUNCTION_DISABLED_MSG);
      }


    } else if (request.getParameter("stats") != null
        && request.getParameter("stats").equals("part") && request.getParameter(TOPIC) != null) {

      String topicIdStr = request.getParameter(TOPIC);

      BigInteger topicId = new BigInteger(topicIdStr);

      DateMidnight date = DateUtils.getCurrentMidnight();

      String dateStr = request.getParameter("date");

      if (dateStr != null) {

        date = DateUtils.parseToMidnightDefTZ(dateStr, DateUtils.YYYY_MM_DD_FORMAT);

      }


      String periodStr = request.getParameter("period");
      String typeStr = request.getParameter("type");

      if (typeStr != null) {

        if (periodStr == null) {
          periodStr = "1";
        }
        Integer period = Integer.parseInt(periodStr);

        PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type =
            PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p;

        if (typeStr.equals("terms")) {
          type = PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p;
        } else if (typeStr.equals("bi_terms")) {
          type = PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_bi_terms_p;
        } else if (typeStr.equals("tri_terms")) {
          type = PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_tri_terms_p;
        }

        List<PeriodTermAggregate> statsL =
            termService.getAggregationStatsPart(topicId, date.toDateTime(), period, type);

        writer.write("<h4>[ Preaggregation statistics for topic: " + topicId + ", date: "
            + DateUtils.printDateTZ(date) + " ]</h4><br> Size:" + statsL.size() + " :"
            + statsL.toString());

      } else {

        List<PeriodTermAggregate> statsL =
            termService.getAggregationStatsForTopicDate(topicId, date.toDateTime());

        writer.write("<h4>[ Preaggregation statistics for topic: " + topicId + ", date: "
            + DateUtils.printDateTZ(date) + " ]</h4><br> Size:" + statsL.size() + " :"
            + statsL.toString());
      }


    }
  }

}
