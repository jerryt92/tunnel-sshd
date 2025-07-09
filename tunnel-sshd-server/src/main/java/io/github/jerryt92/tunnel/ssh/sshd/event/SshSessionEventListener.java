package io.github.jerryt92.tunnel.ssh.sshd.event;

import java.io.IOException;

import io.github.jerryt92.tunnel.ssh.sshd.service.SshSessionService;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SshSessionEventListener implements SessionListener {
    private static final Logger log = LoggerFactory.getLogger(SshSessionEventListener.class);
    @Autowired
    private SshSessionService sshSessionService;

    public void sessionCreated(Session session) {
        try {
            this.sshSessionService.addSession(session);
        } catch (Throwable e) {
            log.error("", e);

            try {
                session.close();
            } catch (IOException ex) {
                log.error("", ex);
            }
        }

    }

    public void sessionClosed(Session session) {
        this.sshSessionService.removeSession(session);
    }
}
