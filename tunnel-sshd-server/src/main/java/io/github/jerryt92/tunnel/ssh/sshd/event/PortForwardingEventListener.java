package io.github.jerryt92.tunnel.ssh.sshd.event;

import io.github.jerryt92.tunnel.ssh.sshd.util.net.ip.Ip4Address;
import io.github.jerryt92.tunnel.ssh.sshd.util.net.ip.Ip6Address;
import io.github.jerryt92.tunnel.ssh.sshd.util.net.ip.IpAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class PortForwardingEventListener implements org.apache.sshd.common.forward.PortForwardingEventListener {
    @Override
    public void establishingExplicitTunnel(Session session, SshdSocketAddress local, SshdSocketAddress remote, boolean localForwarding) throws IOException {
        // 开始建立隧道
        org.apache.sshd.common.forward.PortForwardingEventListener.super.establishingExplicitTunnel(session, local, remote, localForwarding);
    }

    @Override
    public void establishedExplicitTunnel(Session session, SshdSocketAddress local, SshdSocketAddress remote, boolean localForwarding, SshdSocketAddress boundAddress, Throwable reason) throws IOException {
        // 隧道建立成功
        InetSocketAddress localInetSocketAddress = boundAddress.toInetSocketAddress();
        if (localInetSocketAddress.getAddress() instanceof Inet6Address) {
            String v6address = localInetSocketAddress.getAddress().getHostAddress();
            Ip6Address ip6Address = IpAddress.valueOf(v6address).getIp6Address();
            if (ip6Address.compareTo(Ip6Address.valueOf("::")) == 0) {
                localInetSocketAddress = new InetSocketAddress(Ip4Address.valueOf("0.0.0.0").toInetAddress(), localInetSocketAddress.getPort());
            }
        }
        log.info("隧道建立成功");
        log.info("会话客户端地址：{}，会话服务端地址：{}", session.getRemoteAddress(), session.getLocalAddress());
        log.info("隧道建立成功，服务端请求绑定地址：{}，服务端实际绑定地址：{}", local, localInetSocketAddress.getHostString() + ":" + localInetSocketAddress.getPort());
        org.apache.sshd.common.forward.PortForwardingEventListener.super.establishedExplicitTunnel(session, local, remote, localForwarding, boundAddress, reason);
    }

    @Override
    public void tearingDownExplicitTunnel(Session session, SshdSocketAddress address, boolean localForwarding, SshdSocketAddress remoteAddress) throws IOException {
        // 开始拆除隧道
        org.apache.sshd.common.forward.PortForwardingEventListener.super.tearingDownExplicitTunnel(session, address, localForwarding, remoteAddress);
    }

    @Override
    public void tornDownExplicitTunnel(Session session, SshdSocketAddress address, boolean localForwarding, SshdSocketAddress remoteAddress, Throwable reason) throws IOException {
        // 隧道拆除成功
        org.apache.sshd.common.forward.PortForwardingEventListener.super.tornDownExplicitTunnel(session, address, localForwarding, remoteAddress, reason);
    }

    @Override
    public void establishingDynamicTunnel(Session session, SshdSocketAddress local) throws IOException {
        // 开始建立动态隧道
        org.apache.sshd.common.forward.PortForwardingEventListener.super.establishingDynamicTunnel(session, local);
    }

    @Override
    public void establishedDynamicTunnel(Session session, SshdSocketAddress local, SshdSocketAddress boundAddress, Throwable reason) throws IOException {
        // 动态隧道建立成功
        org.apache.sshd.common.forward.PortForwardingEventListener.super.establishedDynamicTunnel(session, local, boundAddress, reason);
    }

    @Override
    public void tearingDownDynamicTunnel(Session session, SshdSocketAddress address) throws IOException {
        // 开始拆除动态隧道
        org.apache.sshd.common.forward.PortForwardingEventListener.super.tearingDownDynamicTunnel(session, address);
    }

    @Override
    public void tornDownDynamicTunnel(Session session, SshdSocketAddress address, Throwable reason) throws IOException {
        // 动态隧道拆除成功
        org.apache.sshd.common.forward.PortForwardingEventListener.super.tornDownDynamicTunnel(session, address, reason);
    }
}
