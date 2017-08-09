package com.mio.hystrix.service;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CommandThatFailsFast extends HystrixCommand<String> {

	private final boolean throwException;

	public CommandThatFailsFast(boolean throwException) {
		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
		this.throwException = throwException;
	}

	/**
	 * 这个方法里面封装了正常的逻辑，我们可以传入正常的业务逻辑
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	protected String run() throws Exception {

		// 如果为true，这个方法丢出异常，如果为false就返回字符串
		if (throwException) {
			throw new RuntimeException("failure from CommandThatFailsFast");
		} else {
			return "success";
		}
	}

	/**
	 * 这个方法中定义了出现异常时, 默认返回的值(相当于服务的降级)。
	 * 
	 * @return
	 */
	@Override
	protected String getFallback() {
		return "default value";
	}

}
