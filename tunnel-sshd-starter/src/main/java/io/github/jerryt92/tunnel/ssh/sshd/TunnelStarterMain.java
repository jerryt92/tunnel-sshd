package io.github.jerryt92.tunnel.ssh.sshd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TunnelStarterMain {
    public static void main(String[] args) {
        System.out.println();
        System.out.println("Starting...");
        SpringApplication springApplication = new SpringApplication(TunnelStarterMain.class);
        springApplication.setBannerMode(Mode.OFF);
        springApplication.run(args);
    }
}
