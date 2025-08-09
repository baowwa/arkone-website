package com.arkone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ArkOne应用启动类
 * 
 * @author ArkOne
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("com.arkone.mapper")
@EnableAsync
@EnableScheduling
public class ArkoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArkoneApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║                                                               ║
            ║    █████╗ ██████╗ ██╗  ██╗ ██████╗ ███╗   ██╗███████╗        ║
            ║   ██╔══██╗██╔══██╗██║ ██╔╝██╔═══██╗████╗  ██║██╔════╝        ║
            ║   ███████║██████╔╝█████╔╝ ██║   ██║██╔██╗ ██║█████╗          ║
            ║   ██╔══██║██╔══██╗██╔═██╗ ██║   ██║██║╚██╗██║██╔══╝          ║
            ║   ██║  ██║██║  ██║██║  ██╗╚██████╔╝██║ ╚████║███████╗        ║
            ║   ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝╚══════╝        ║
            ║                                                               ║
            ║                  ArkOne Backend Started!                     ║
            ║                                                               ║
            ╚═══════════════════════════════════════════════════════════════╝
            
            """);
    }
}