package com.mio.feign.service;

import feign.RequestLine;

public interface FooClient {
	@RequestLine("GET /hello")
//	@RequestMapping(method = RequestMethod.GET, value = "/hello")
	String hello();
}
