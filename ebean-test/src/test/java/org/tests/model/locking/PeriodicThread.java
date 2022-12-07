/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.locking;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tests.query.other.TestSelfParentConcurrent;

/**
 * A periodic thread. The thread will execute the <code>target</code> with a delay of <code>waitMillisBetweenRuns</code>.
 *
 * It also supports immedate wakeup.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class PeriodicThread extends Thread {

  Logger log = LoggerFactory.getLogger(PeriodicThread.class);
	private final long waitMillisBetweenRuns;
	private volatile boolean running = true;
	private boolean notified = false;

	/**
	 * Constructor.
	 */
	public PeriodicThread(final Runnable target, final String name, final long waitMillisBetweenRuns) {
		super(target, name);
		this.waitMillisBetweenRuns = waitMillisBetweenRuns;
	}


	@Override
	public final void run() {
		while (running) {
			try {
				super.run();
			} catch (Throwable t) {
				log.error("Unhandled exception. Will keep running.", t);
				//statsRegistry.registerUnexpectedError();
			}
			if (waitMillisBetweenRuns > 0) {
				try {
					synchronized (this) {
						if (!running) {
							return;
						}
						if (!notified) {
							wait(waitMillisBetweenRuns);
						}
						notified = false;
					}
				} catch (InterruptedException interruptedException) {
					if (running) {
						log.error("Unexpected interruption of thread. Will keep running.", interruptedException);
					} else {
						log.debug("Thread '{}' interrupted due to shutdown.", Thread.currentThread().getName());
					}
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	public synchronized void wakeup() {
		notified = true;
		notifyAll();
	}

	/**
	 * Shutdown the periodicthread. Thread is gracefully notified and after <code>millis</code>, it is interrupted. This is repeated 5 times
	 * if the thread does not terminate.
	 */
	public synchronized void shutdown(final int millis) throws InterruptedException {
		running = false;
		for (int i = 1; i <= 5; i++) {
			notify();
			try {
				join(millis);
			} catch (InterruptedException ie) {
				// ignore. This may happen on an early restart of logback
			}
			interrupt();
			if (isAlive()) {
				StringBuilder sb = new StringBuilder("Could not shutdown " + getName() + " in attempt " + i + " of 5");
				for (StackTraceElement stack : getStackTrace()) {
					sb.append("\n\t").append(stack);
				}
				log.warn(sb.toString());
			} else {
				return;
			}
		}
	}
}
