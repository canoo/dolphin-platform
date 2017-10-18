package com.canoo.impl.platform.core;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.canoo.dp.impl.platform.core.SimpleDolphinPlatformThreadFactory;

public class SimpleDolphinPlatformthreadFactoryTest {

	@Test
	public void testPlatformThread() {

		SimpleDolphinPlatformThreadFactory sdptf = new SimpleDolphinPlatformThreadFactory();
		Runnable r = new SimpleTask("High Priority Task");
		assertEquals(sdptf.newThread(r).getThreadGroup().getName(), "Dolphin Platform executors");
	}
}

class SimpleTask implements Runnable {
	String s = null;

	public SimpleTask(String s) {
		this.s = s;
	}

	@Override
	public void run() {
	}
}