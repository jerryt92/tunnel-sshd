package io.github.jerryt92.tunnel.ssh.sshd.sshd;

import io.github.jerryt92.tunnel.ssh.sshd.common.ShellService;
import io.github.jerryt92.tunnel.ssh.sshd.config.SshdConfig;
import io.github.jerryt92.tunnel.ssh.sshd.crypto.Ed25519KeyPair;
import io.github.jerryt92.tunnel.ssh.sshd.crypto.RSAKeyPair;
import io.github.jerryt92.tunnel.ssh.sshd.event.MyIoServiceEventListener;
import io.github.jerryt92.tunnel.ssh.sshd.util.Ed25519Util;
import io.github.jerryt92.tunnel.ssh.sshd.util.RSAUtil;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.forward.PortForwardingEventListener;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.common.signature.BuiltinSignatures;
import org.apache.sshd.common.signature.Signature;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.forward.ForwardingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executor;

@Component
public class SshdServer {
    private static final Logger log = LoggerFactory.getLogger(SshdServer.class);
    @Autowired
    private ShellService shellService;
    @Autowired
    private SshdConfig sshdConfig;
    @Autowired
    private PasswordAuthenticator passwordAuthenticator;
    @Autowired
    private PublickeyAuthenticator publickeyAuthenticator;
    @Autowired
    private SessionListener sessionListener;
    @Autowired
    private PortForwardingEventListener nePortForwardingEventListener;
    @Autowired
    private MyIoServiceEventListener myIoServiceEventListener;
    private static SshServer sshdInstance;
    @Autowired
    @Qualifier("sshdExecutor")
    private Executor sshdExecutor;

    @PostConstruct
    public void initSshd() {
        sshdExecutor.execute(() -> {
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
                List<NamedFactory<Signature>> signatureFactories = new ArrayList<>();
                signatureFactories.add(BuiltinSignatures.rsa);
                signatureFactories.add(BuiltinSignatures.rsaSHA256);
                signatureFactories.add(BuiltinSignatures.rsaSHA256_cert);
                signatureFactories.add(BuiltinSignatures.rsaSHA512);
                signatureFactories.add(BuiltinSignatures.rsaSHA512_cert);
                signatureFactories.add(BuiltinSignatures.ed25519);
                signatureFactories.add(BuiltinSignatures.ed25519_cert);
                sshdInstance.setSignatureFactories(signatureFactories);
                // 设置密码验证
                sshdInstance.setPasswordAuthenticator(passwordAuthenticator);
                // 设置公钥验证
                sshdInstance.setPublickeyAuthenticator(publickeyAuthenticator);
                // 设置shell
//                sshdInstance.setShellFactory(new ProcessShellFactory("/bin/sh", "-i"));
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
                sshdInstance.addPortForwardingEventListener(nePortForwardingEventListener);
                sshdInstance.setIoServiceEventListener(myIoServiceEventListener);
                sshdInstance.start();
                this.shellService.start();
                System.out.println("SSHD started on port : " + this.sshdConfig.sshdPort);
            } catch (Throwable e) {
                log.error("Failed to start SSHD server", e);
            }
        });
    }
}
