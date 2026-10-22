package com.example.caching_proxy.config;

import com.example.caching_proxy.DTO.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class serverPortConfig {
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(){
        System.out.println("My port config "+Server.getInstance().getPort());
        return factory -> factory.setPort(Server.getInstance().getPort());
    }
}
