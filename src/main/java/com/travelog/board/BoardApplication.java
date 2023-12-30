package com.travelog.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@PropertySource(value = {"classpath:application-elastic.yml"}, factory = EnvConfig.class)
class BoardApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BoardApplication.class, args);
	}

}
