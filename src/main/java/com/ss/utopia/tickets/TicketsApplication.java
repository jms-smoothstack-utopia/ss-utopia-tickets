package com.ss.utopia.tickets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class TicketsApplication {

  public static void main(String[] args) {
    SpringApplication.run(TicketsApplication.class, args);
  }

  @Profile("ecs")
  @Bean
  public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils)
      throws UnknownHostException {
    var config = new EurekaInstanceConfigBean(inetUtils);
    config.setIpAddress(InetAddress.getLocalHost().getHostAddress());
    config.setNonSecurePort(8082);
    config.setPreferIpAddress(true);
    return config;
  }
}
