/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 */

package de.fzi.cjunit.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import de.fzi.cjunit.testutils.TestException;


public class TestTestThread {

	@Test
	public void testRunWithoutException() {
		TestThread thread = new TestThread() {
			public void explodableRun() throws Throwable {}
		};
		thread.run();

		assertThat(thread.exceptionInThread, nullValue());
	}

	@Test
	public void testRunStoresException() {
		final Throwable exception = new TestException();

		TestThread thread = new TestThread() {
			public void explodableRun() throws Throwable {
				throw exception;
			}
		};
		thread.run();

		assertThat(thread.exceptionInThread, equalTo(exception));
	}

	@Test
	public void testJoinExplosivelyWithoutException() throws Throwable {
		TestThread thread = new TestThread();

		thread.start();
		thread.joinExplosively();
	}

	@Test
	public void testJoinExplosively() throws Throwable {
		Throwable exception = new TestException();
		TestThread thread = new TestThread();
		thread.exceptionInThread = exception;

		thread.start();
		try {
			thread.joinExplosively();
			throw new Exception("No exception was thrown from joinExplosively()");
		} catch (Throwable t) {
			assertThat(t, equalTo(exception));
		}
	}
}
