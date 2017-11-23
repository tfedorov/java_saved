package com.tfedorov.social.topic.processing;

import java.util.ArrayList;
import java.util.List;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import org.json.JSONException;
import org.json.JSONObject;

import com.tfedorov.social.concurrency.Task;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextImpl;

import etm.core.monitor.EtmPoint;

public class TopicTaskExecutionHandler
    extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  private ProcessingHandler<GeneralProcessingContext> repeatedSuccessor;

  /**
   * For test purposes only
   * 
   * @return the repeatedSuccessor
   */
  public ProcessingHandler<GeneralProcessingContext> getRepeatedSuccessor() {
    return repeatedSuccessor;
  }

  //
  public TopicTaskExecutionHandler(ProcessingHandler<GeneralProcessingContext> repeatedSuccessor,
      ProcessingHandler<GeneralProcessingContext> exitSuccessor) {
    super(exitSuccessor);
    this.repeatedSuccessor = repeatedSuccessor;
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {
    List<Task> list = getTaskList(context);
    context.getServicesContext().getTaskExecutionService().execute(list);
  }

  protected List<Task> getTaskList(final GeneralProcessingContext generalContext) {
    // TODO: revisit code to prepare contextslist once after topics update
    List<TopicInfo> list =
        (List<TopicInfo>) generalContext.get(GeneralProcessingContextImpl.TOPICS_INFO_LIST);
    List<Task> taskList = new ArrayList<Task>(list.size());
    // generate tasks list
    for (TopicInfo topicInfo : list) {
      final TopicProcessingContext topicContext = new TopicProcessingContext(topicInfo);
      // create copy of context to prevent from synchronization problems
      final GeneralProcessingContext copyContext = generalContext.copy();
      copyContext.add(topicContext.getContextName(), topicContext);

      taskList.add(new Task() {
        @Override
        public void execute() {
          EtmPoint perfPoint1 = getPerformancePoint(".executeTask()");

          try {
            repeatedSuccessor.process(copyContext);
          } finally {
            perfPoint1.collect();
          }
        }
      });
    }

    return taskList;
  }

  @Override
  public Class<?> getClazz() {
    return TopicTaskExecutionHandler.class;
  }

  @Override
  public JSONObject returnJson() throws JSONException {
    JSONObject point = new JSONObject();
    point.put("cname", this.getClass().getName());
    point.put("repeat", repeatedSuccessor.returnJson());
    point.put("quit", getSuccessor().returnJson());

    return point;
  }
}
