package com.example.caching_proxy;
import com.example.caching_proxy.DTO.Server;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class CachingProxyApplication {


	public static void main(String[] args) {
		for(int i=0;i<args.length;i++){
			if(args[i].equalsIgnoreCase("--origin") && i+1< args.length){
				Server.getInstance().setOrigin(args[i+1]);
			}
			if(args[i].equalsIgnoreCase("--port") && i+1< args.length){
				Server.getInstance().setPort(Integer.parseInt(args[i+1]));
			}
		}
		System.out.println("🚀 Proxy started on port " + Server.getInstance().getPort() + " forwarding to origin: " + Server.getInstance().getOrigin());
		SpringApplication.run(CachingProxyApplication.class, args);
	}
}
