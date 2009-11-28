/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.outside;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.fzi.cjunit.testutils.TestException;

public class JPFInvokerTest {

	@Test
	public void getExceptionClassName() {
		assertThat(new JPFInvoker().getExceptionClassName(RuntimeException.class),
				equalTo(RuntimeException.class.getName()));
	}

	@Test
	public void getExceptionClassNameWithNullArgument() {
		assertThat(new JPFInvoker().getExceptionClassName(null),
				equalTo(""));
	}

	@Test
	public void createJPFArgs() throws Throwable {
		List<Method> befores = new ArrayList<Method>();
		befores.add(String.class.getMethod("hashCode"));
		befores.add(String.class.getMethod("notify"));
		List<Method> afters = new ArrayList<Method>();
		afters.add(String.class.getMethod("notifyAll"));
		afters.add(String.class.getMethod("wait"));
		String[] args = new JPFInvoker().createJPFArgs(new String(),
				String.class.getMethod("toString"),
				befores, afters, Exception.class);
		assertThat(args, hasItemInArray(
				"de.fzi.cjunit.jpf.inside.TestWrapper"));
		assertThat(args, hasItemInArray(
				"--testclass=java.lang.String"));
		assertThat(args, hasItemInArray(
			"--test=method=toString,exception=java.lang.Exception"));
		assertThat(args, hasItemInArray("--beforemethod=hashCode"));
		assertThat(args, hasItemInArray("--beforemethod=notify"));
		assertThat(args, hasItemInArray("--aftermethod=notifyAll"));
		assertThat(args, hasItemInArray("--aftermethod=wait"));
	}

	@Test
	public void getTestResultOfSucceededTest() {
		JPFInvoker jpfInvoker = new JPFInvoker();
		jpfInvoker.rc = new ResultCollector(null, null) {
			@Override
			public boolean getTestResult() {
				return true;
			}
		};

		assertThat("test result", jpfInvoker.getTestResult(),
				equalTo(true));
	}

	@Test
	public void getTestResultOfFailedTest() {
		JPFInvoker jpfInvoker = new JPFInvoker();
		jpfInvoker.rc = new ResultCollector(null, null) {
			@Override
			public boolean getTestResult() {
				return false;
			}
		};

		assertThat("test result", jpfInvoker.getTestResult(),
				equalTo(false));
	}

	@Test
	public void checkResultOfSucceededTest() throws Throwable {
		JPFInvoker jpfInvoker = new JPFInvoker();
		jpfInvoker.rc = new ResultCollector(null, null) {
			@Override
			public boolean getTestResult() {
				return true;
			}
		};

		jpfInvoker.checkResult();
	}

	@Test(expected=TestException.class)
	public void checkResultOfFailedTest() throws Throwable {
		JPFInvoker jpfInvoker = new JPFInvoker();
		jpfInvoker.rc = new ResultCollector(null, null) {
			@Override
			public boolean getTestResult() {
				return false;
			}
			@Override
			public Throwable getException() {
				return new TestException();
			}
		};

		jpfInvoker.checkResult();
	}
}
