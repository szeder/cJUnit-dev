/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 */

package de.fzi.cjunit.util;

/**
 * A {@link Thread} subclass to make handling exceptions in threads easier.
 * <p>
 * Not necessary for ConcurrentTests.
 * <p>
 * Sometimes one needs to write a concurrent unit test in which the code
 * executed in (one of) the created thread(s) can throw an exception if
 * something goes wrong.  But an exception thrown in a thread will be caught
 * by the JVM, not by the testing framework, and, consequently, the testing
 * framework will not know about the test's failure.  Furthermore, the code
 * executed in a created thread might throw checked exceptions, but since
 * {@link Thread#run()} doesn't allow checked exceptions the developer must
 * deal with those exceptions.
 *
 * <pre>
 *   TestThread t = new TestThread() {
 *       public void explodableRun() throws Throwable {
 *           throw new Throwable("Something always goes wrong...");
 *       }
 *   };
 *   t.start();
 *   t.joinExplosively();    // rethrows the Throwable created in the thread
 * </pre>
 * @see Thread
 */
public class TestThread extends Thread {

	protected Throwable exceptionInThread;

	/**
	 * This method is invoked by {@link Thread#run()}.
	 * Subclasses of <code>TestThread</code> should override this method.
	 *
	 * @throws Throwable
	 */
	public void explodableRun() throws Throwable {}

	/**
	 * Runs {@link #explodableRun()} and stores the exception it might
	 * throw to be checked later by {@link #joinExplosively()}.
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		try {
			explodableRun();
		} catch (Throwable t) {
			exceptionInThread = t;
		}
	}

	/**
	 * Waits for this thread to die, and checks whether it threw an
	 * exception, in which case that exception is rethrown in the thread
	 * that invoked <code>joinExplosively()</code>.
	 *
	 * @exception	Throwable	if the thread threw an exception.
	 * @exception	InterruptedException	if any thread has interrupted
	 *		the current thread.  The <i>interrupted status</i> of
	 *		the current thread is cleared when this exception is
	 *		thrown.
	 * @see Thread#join()
	 */
	public void joinExplosively() throws InterruptedException, Throwable {
		joinExplosively(0, 0);
	}

	/**
	 * Waits at most <code>millis</code> milliseconds for this thread to
	 * die, and checks whether it threw an exception, in which case that
	 * exception is rethrown in the thread that invoked
	 * <code>joinExplosively(long)</code>. A timeout of <code>0</code>
	 * means to wait forever.
	 *
	 * @param	millis	the time to wait in milliseconds.
	 * @exception	Throwable	if the thread threw an exception.
	 * @exception	InterruptedException	if any thread has interrupted
	 *		the current thread.  The <i>interrupted status</i> of
	 *		the current thread is cleared when this exception is
	 *		thrown.
	 * @see TestThread#join()
	 * @see Thread#join(long)
	 */
	public void joinExplosively(long millis)
			throws InterruptedException, Throwable {
		joinExplosively(millis, 0);
	}

	/**
	 * Waits at most <code>millis</code> milliseconds plus
	 * <code>nanos</code> nanoseconds for this thread to die, and checks
	 * whether it threw an exception, in which case that exception is
	 * rethrown in the thread that invoked
	 * <code>joinExplosively(long, int)</code>.
	 *
	 * @param	millis	the time to wait in milliseconds.
	 * @param	nanos	0-999999 additional nanoseconds to wait.
	 * @exception	Throwable	if the thread threw an exception.
	 * @exception	IllegalArgumentException	if the value of
	 *		millis is negative, or the value of nanos is not in
	 *		the range 0-999999.
	 * @exception	InterruptedException	if any thread has interrupted
	 *		the current thread.  The <i>interrupted status</i> of
	 *		the current thread is cleared when this exception is
	 *		thrown.
	 * @see Thread#join(long, int)
	 */
	public void joinExplosively(long millis, int nanos)
			throws InterruptedException, Throwable {
		super.join(millis, nanos);

		if (exceptionInThread != null)
			throw exceptionInThread;
	}

	/**
	 * @see TestThread
	 * @see Thread#Thread()
	 */
	public TestThread() {
	}

	/**
	 * @see TestThread
	 * @see Thread#Thread(String)
	 */
	public TestThread(String name) {
		super(name);
	}

	/**
	 * @see TestThread
	 * @see Thread#Thread(ThreadGroup, String)
	 */
	public TestThread(ThreadGroup group, String name) {
		super(group, name);
	}
}
