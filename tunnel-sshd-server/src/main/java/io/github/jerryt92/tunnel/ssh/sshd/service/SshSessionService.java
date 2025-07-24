package io.github.jerryt92.tunnel.ssh.sshd.service;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.Getter;
import org.apache.sshd.common.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SshSessionService {
    private static final Logger log = LoggerFactory.getLogger(SshSessionService.class);
    @Getter
    private ConcurrentLinkedQueue<Session> sshSessions = new ConcurrentLinkedQueue();
    private ConcurrentHashMap<Session, InetSocketAddress> sessionForwardingAddress = new ConcurrentHashMap();

    public String getSessionByIndex(int index) {
        return this.sshSessions.toArray()[index].toString();
    }

    public void closeSessionByIndex(int index) {
        if (index >= 0 && index < this.sshSessions.size()) {
            Session session = (Session)this.sshSessions.toArray()[index];

            try {
                session.close();
                System.out.println("Session closed");
            } catch (Exception e) {
                log.error("Close session error", e);
                System.out.println("Close session error");
            }

        } else {
            System.out.println("未知的索引：“" + index + "”");
        }
    }

    public void addSession(Session session) throws RuntimeException {
        this.sshSessions.add(session);
    }

    public void addForwardingAddress(Session session, InetSocketAddress address) {
        this.sessionForwardingAddress.put(session, address);
    }

    public void removeSession(Session session) {
        this.sshSessions.remove(session);
        this.sessionForwardingAddress.remove(session);
    }

    public void showSshSessions() {
        System.out.println("   索引   |   客户端地址   |   服务端地址   |   端口转发功能绑定的服务端地址   ");
        System.out.println();
        int index = 0;

        for(Session session : this.sshSessions) {
            System.out.println("   " + index + "   |   " + session.getRemoteAddress().toString().replace("/", "") + "   |   " + session.getLocalAddress().toString().replace("/", "") + "   |   " + (this.sessionForwardingAddress.get(session) == null ? "-" : (Serializable)this.sessionForwardingAddress.get(session)).toString().replace("/", ""));
            ++index;
        }

    }
}

