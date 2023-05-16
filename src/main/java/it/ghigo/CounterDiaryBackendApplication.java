package it.ghigo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import it.ghigo.interceptor.LoggingInterceptor;
import it.ghigo.repository.CounterStatsFinder;

@SpringBootApplication
public class CounterDiaryBackendApplication implements WebMvcConfigurer {

	@Autowired
	private LoggingInterceptor loggingInterceptor;

	public static void main(String[] args) {
		SpringApplication.run(CounterDiaryBackendApplication.class, args);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggingInterceptor);
	}

	@Bean
	CounterStatsFinder getCounterStatsFinder() {
		return new CounterStatsFinder();
	}
}