/**
 * 
 */
package com.tfedorov.social.twitter.tracing;

/**
 * @author tfedorov
 *
 */
public class CurrentPerformanceBean {
	
	private double currentSpeed;
	private int processedNumeber;
	private long strartMonitoringMS;
	private long endMonitoringMS;
	
	public CurrentPerformanceBean(double currentSpeed, int precessedNumeber,
			long strartMonitoringMS, long endMonitoringMS) {
		super();
		this.currentSpeed = currentSpeed;
		this.processedNumeber = precessedNumeber;
		this.strartMonitoringMS = strartMonitoringMS;
		this.endMonitoringMS = endMonitoringMS;
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	public int getProcessedNumeber() {
		return processedNumeber;
	}

	public long getStrartMonitoringMS() {
		return strartMonitoringMS;
	}

	public long getEndMonitoringMS() {
		return endMonitoringMS;
	}
}
