package io.github.jerryt92.tunnel.ssh.sshd.sshd;

import io.github.jerryt92.tunnel.ssh.sshd.common.ShellService;
import io.github.jerryt92.tunnel.ssh.sshd.common.ShellServiceShellFactory;
import io.github.jerryt92.tunnel.ssh.sshd.config.SshdConfig;
import io.github.jerryt92.tunnel.ssh.sshd.crypto.Ed25519KeyPair;
import io.github.jerryt92.tunnel.ssh.sshd.crypto.RSAKeyPair;
import io.github.jerryt92.tunnel.ssh.sshd.event.DynamicForwardingListener;
import io.github.jerryt92.tunnel.ssh.sshd.event.MyIoServiceEventListener;
import io.github.jerryt92.tunnel.ssh.sshd.util.Ed25519Util;
import io.github.jerryt92.tunnel.ssh.sshd.util.RSAUtil;
import org.apache.sshd.common.forward.PortForwardingEventListener;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.core.CoreModuleProperties;
import org.apache.sshd.server.ServerBuilder;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.forward.ForwardingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

@Component
public class SshdServer {
    private static final Logger log = LoggerFactory.getLogger(SshdServer.class);
    @Autowired
    private SshdConfig sshdConfig;
    @Autowired
    private PasswordAuthenticator passwordAuthenticator;
    @Autowired
    private PublickeyAuthenticator publickeyAuthenticator;
    @Autowired
    private SessionListener sessionListener;
    @Autowired
    private PortForwardingEventListener portForwardingEventListener;
    @Autowired
    private DynamicForwardingListener dynamicForwardingListener;
    @Autowired
    private MyIoServiceEventListener myIoServiceEventListener;
    private static SshServer sshdInstance;
    @Autowired
    private ShellService shellService;

    @PostConstruct
    public void initSshd() {
        try {
            PublicKey ed25519PublicKey = Ed25519Util.getPublicKeyFromBytes(Base64.getDecoder().decode(Ed25519KeyPair.PUBLIC_KEY));
            PrivateKey ed25519PrivateKey = Ed25519Util.getPrivateKeyFromBytes(Base64.getDecoder().decode(Ed25519KeyPair.PRIVATE_KEY));
            PublicKey rsaPublicKey = RSAUtil.getPublicKeyFromBytes(Base64.getDecoder().decode(RSAKeyPair.PUBLIC_KEY));
            PrivateKey rsaPrivateKey = RSAUtil.getPrivateKeyFromBytes(Base64.getDecoder().decode(RSAKeyPair.PRIVATE_KEY));
            KeyPairProvider keyPairProvider = KeyPairProvider.wrap(
                    new KeyPair(rsaPublicKey, rsaPrivateKey),
                    new KeyPair(ed25519PublicKey, ed25519PrivateKey)
            );
            sshdInstance = SshServer.setUpDefaultServer();
            sshdInstance.setHost(sshdConfig.bindAddress);
            sshdInstance.setPort(sshdConfig.sshdPort);
            sshdInstance.setKeyPairProvider(keyPairProvider);
            // 设置签名工厂
            // 设置支持的签名算法
            sshdInstance.setSignatureFactories(new ArrayList<>(ServerBuilder.DEFAULT_SIGNATURE_PREFERENCE));
            if (sshdConfig.allowPassword) {
                // 设置密码验证
                sshdInstance.setPasswordAuthenticator(passwordAuthenticator);
            }
            if (sshdConfig.allowPublicKey) {
                // 设置公钥验证
                sshdInstance.setPublickeyAuthenticator(publickeyAuthenticator);
            }
            // 设置shell
            setShell();
            // 设置端口转发
            sshdInstance.setForwardingFilter(new ForwardingFilter() {
                @Override
                public boolean canForwardAgent(Session session, String s) {
                    // 转发Agent（SSH Agent是一个用于管理密钥的程序）
                    return sshdConfig.allowForwardAgent;
                }

                @Override
                public boolean canForwardX11(Session session, String s) {
                    // 转发X11（X11是一个用于图形化显示的协议）
                    return sshdConfig.allowForwardX11;
                }

                @Override
                public boolean canListen(SshdSocketAddress address, Session session) {
                    // 客户端远程转发（-R）
                    return sshdConfig.allowClientRemoteForward;
                }

                @Override
                public boolean canConnect(Type type, SshdSocketAddress address, Session session) {
                    // 客户端本地转发（-L）（建议关闭，因为这会允许客户端连接到服务器的任何端口）
                    return sshdConfig.allowClientLocalForward;
                }
            });
            // 添加会话事件监听器
            sshdInstance.addSessionListener(sessionListener);
            // 添加端口转发事件监听器
            sshdInstance.addPortForwardingEventListener(portForwardingEventListener);
            sshdInstance.addChannelListener(dynamicForwardingListener);
            sshdInstance.setIoServiceEventListener(myIoServiceEventListener);
            if (sshdConfig.disableRekeyBytesLimit) {
                // 禁用基于流量的重协商
                sshdInstance.getProperties().put(CoreModuleProperties.REKEY_BYTES_LIMIT.getName(), -1L);
            }
            // 窗口大小配置 1MB
            sshdInstance.getProperties().put(CoreModuleProperties.WINDOW_SIZE.getName(), 1024 * 1024);
            // 单个数据包最大大小 64KB
            sshdInstance.getProperties().put(CoreModuleProperties.MAX_PACKET_SIZE.getName(), 2 * CoreModuleProperties.DEFAULT_MAX_PACKET_SIZE);
            // 包大小上限 1GB
            sshdInstance.getProperties().put(CoreModuleProperties.LIMIT_PACKET_SIZE.getName(), 2 * CoreModuleProperties.DEFAULT_LIMIT_PACKET_SIZE);
            sshdInstance.start();
            System.out.println("SSHD started on port : " + this.sshdConfig.sshdPort);
            shellService.start();
        } catch (Throwable e) {
            log.error("Failed to start SSHD server", e);
        }
    }

    public static void setShell() {
        sshdInstance.setShellFactory(new ShellServiceShellFactory());
    }
}