package io.github.jerryt92.tunnel.ssh.sshd.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class CommonConstants {
    @Getter
    private static String springApplicationName;

    @Value("${spring.application.name}")
    private void setSpringApplicationName(String springApplicationName) {
        CommonConstants.springApplicationName = springApplicationName;
    }
}
