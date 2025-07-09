package io.github.jerryt92.tunnel.ssh.sshd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SshdConfig {
    @Value("${tunnel.sshd.bind-address}")
    public String bindAddress;
    @Value("${tunnel.sshd.port}")
    public int sshdPort;
    @Value("${tunnel.sshd.allow-forward-agent}")
    public boolean allowForwardAgent;
    @Value("${tunnel.sshd.allow-forward-x11}")
    public boolean allowForwardX11;
    @Value("${tunnel.sshd.allow-client-remote-forward}")
    public boolean allowClientRemoteForward;
    @Value("${tunnel.sshd.allow-client-local-forward}")
    public boolean allowClientLocalForward;
    @Value("${tunnel.sshd.allow-password}")
    public boolean allowPassword;
    @Value("${tunnel.sshd.username}")
    public String username;
    @Value("${tunnel.sshd.password}")
    public String password;
}
