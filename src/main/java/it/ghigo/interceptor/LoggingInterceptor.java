package it.ghigo.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

	private static final String START_TIME = "LoggingInterceptor.startTime";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		request.setAttribute(START_TIME, System.currentTimeMillis());
		logger.info("Request: " + request.getRequestURI());
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long startTime = (Long) request.getAttribute(START_TIME);
		long endTime = System.currentTimeMillis();
		long executeTime = endTime - startTime;
		logger.info("Response: " + response.getStatus() + " -> " + executeTime + " ms");
	}
}
