/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.runners;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import de.fzi.cjunit.ConcurrentTest;

public class ConcurrentRunner extends BlockJUnit4ClassRunner {

	List<FrameworkMethod> testMethods;

	public ConcurrentRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);
		validateConcurrentTestMethods(errors);
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		if (testMethods == null) {
			testMethods = new ArrayList<FrameworkMethod>(
					super.computeTestMethods());
			testMethods.addAll(computeConcurrentTestMethods());
		}
		return testMethods;
	}

	protected List<FrameworkMethod> computeConcurrentTestMethods() {
		return getTestClass().getAnnotatedMethods(ConcurrentTest.class);
	}

	protected void validateConcurrentTestMethods(List<Throwable> errors) {
		validatePublicVoidNoArgMethods(ConcurrentTest.class, false,
				errors);
	}
}
