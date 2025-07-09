package io.github.jerryt92.tunnel.ssh.sshd.event;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.io.IoAcceptor;
import org.apache.sshd.common.io.IoConnector;
import org.apache.sshd.common.io.IoServiceEventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketAddress;

@Component
@Slf4j
public class MyIoServiceEventListener implements IoServiceEventListener {
    @Override
    public void connectionEstablished(IoConnector connector, SocketAddress local, AttributeRepository context, SocketAddress remote) throws IOException {
        log.info("connectionEstablished, local: {}, remote: {}", local, remote);
        IoServiceEventListener.super.connectionEstablished(connector, local, context, remote);
    }

    @Override
    public void abortEstablishedConnection(IoConnector connector, SocketAddress local, AttributeRepository context, SocketAddress remote, Throwable reason) throws IOException {
        log.info("abortEstablishedConnection, local: {}, remote: {}", local, remote);
        IoServiceEventListener.super.abortEstablishedConnection(connector, local, context, remote, reason);
    }

    @Override
    public void connectionAccepted(IoAcceptor acceptor, SocketAddress local, SocketAddress remote, SocketAddress service) throws IOException {
        log.info("connectionAccepted, local: {}, remote: {}", local, remote);
        IoServiceEventListener.super.connectionAccepted(acceptor, local, remote, service);
    }

    @Override
    public void abortAcceptedConnection(IoAcceptor acceptor, SocketAddress local, SocketAddress remote, SocketAddress service, Throwable reason) throws IOException {
        log.info("abortAcceptedConnection, local: {}, remote: {}", local, remote);
        IoServiceEventListener.super.abortAcceptedConnection(acceptor, local, remote, service, reason);
    }
}
