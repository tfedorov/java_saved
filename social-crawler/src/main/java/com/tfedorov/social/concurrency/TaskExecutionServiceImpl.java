package com.tfedorov.social.concurrency;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("taskExecutionService")
public class TaskExecutionServiceImpl implements
		TaskExecutionService {
	private static final int WAIT_SECONDS = 20;

  private Logger logger = LoggerFactory
			.getLogger(TaskExecutionServiceImpl.class);
	
	private int threadCount;
	
	private ExecutorService executor;
	
	private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

	@Autowired
	public TaskExecutionServiceImpl(
			@Value("${crawler.executor.thread.number:15}") int threadNumber) {
		this.threadCount = threadNumber;
	}

	@Override
	@PostConstruct
	public void init() {
		executor = Executors.newFixedThreadPool(threadCount);
		logger.info("Abstract thread executor initialized! Thread pool size = "	+ threadCount);
	}

	public void reConfigureExecutor(int threadNumber) {
		this.threadCount = threadNumber;
		// stop previous executor
		if (executor != null && !executor.isShutdown()) {
			executor.shutdown();
			try {
				executor.awaitTermination(WAIT_SECONDS, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
			  logger.error(e.getMessage(), e);
			}
		}
		// reConfigurate
		executor = Executors.newFixedThreadPool(threadCount);
	}

	@Override
	public void execute(Collection<Task> tasks) {
		
		EtmPoint perfPoint = getPerformancePoint(".execute()");

		try {
			// create concurrent queue from data list
			Queue<Task> dataList = new LinkedList<Task>(
					tasks);

			CountDownLatch count = new CountDownLatch(dataList.size());
			// add task
			while (!dataList.isEmpty()) {
				executor.execute(new ExecutorTask(dataList, count));
			}
			// wait for all submitted task be finished
			try {
				count.await();
			} catch (InterruptedException e) {
				logger.error("Abstract thread executor start work method interrupted exception!");
			}
		} finally {
			perfPoint.collect();
		}

	}

	@Override
	@PreDestroy
	public void destroy() {
		// stop executor
		if (executor != null && !executor.isShutdown()) {
			executor.shutdown();
			try {
				executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				logger.error("Abstract thread executor destroy method interrupted exception!");
			}
		}
	}
	
	private EtmPoint getPerformancePoint(String name) {
		return performanceMonitor.createPoint(new StringBuilder(TaskExecutionServiceImpl.class.toString()).append(name).toString());
	}
	
	
	class ExecutorTask implements Runnable {
		// some data
		private Task task;
		private CountDownLatch count;

		// constructor with data parameters
		public ExecutorTask(Queue<Task> queue,
				CountDownLatch count) {
			this.task = queue.poll();
			this.count = count;
		}

		// work
		@Override
		public void run() {
			EtmPoint perfPoint = performanceMonitor.createPoint(ExecutorTask.class+".run()"); 
			try {
				task.execute();
			} catch (Exception e) {
				EtmPoint perfPoint1 = performanceMonitor.createPoint(ExecutorTask.class+".run():[EXCEPTION]:"+e.getClass());
				try {
					logger.error("Error within task execution", e);
				} finally {
					perfPoint1.collect();
				}
			} finally {
				EtmPoint perfPoint1 = performanceMonitor.createPoint(ExecutorTask.class+".run():[countDown]:");
				try {
					count.countDown();
				} finally {
					perfPoint1.collect();
				}
				perfPoint.collect();
			}
		}
		
	}


}

