package io.github.jerryt92.tunnel.ssh.sshd.authenticator;

import io.github.jerryt92.tunnel.ssh.sshd.config.SshdConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.session.ServerSession;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SshAuthenticator implements PasswordAuthenticator {
    private final SshdConfig sshdConfig;

    public SshAuthenticator(SshdConfig sshdConfig) {
        this.sshdConfig = sshdConfig;
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession serverSession) throws PasswordChangeRequiredException, AsyncAuthException {
        return sshdConfig.username.equals(username) && sshdConfig.password.equals(password);
    }
}