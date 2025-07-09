package io.github.jerryt92.tunnel.ssh.sshd.authenticator;

import io.github.jerryt92.tunnel.ssh.sshd.config.SshdConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.session.ServerSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SshAuthenticator implements PasswordAuthenticator {
    @Autowired
    private SshdConfig sshdConfig;

    public SshAuthenticator() throws Exception {
//        List<PublicKey> publicKeys = loadPublicKeys();
//        if (publicKeys.isEmpty()) {
//            log.warn("No public keys loaded.");
//        }
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession serverSession) throws PasswordChangeRequiredException, AsyncAuthException {
        if (sshdConfig.allowPassword) {
            return sshdConfig.username.equals(username) && sshdConfig.password.equals(password);
        } else {
            return false;
        }
    }

//    private List<PublicKey> loadPublicKeys() throws Exception {
//        ClassLoader classLoader = PasswordAuthenticator.class.getClassLoader();
//        InputStream authorizedKeys = classLoader.getResourceAsStream("authorized_keys");
//        if (authorizedKeys == null) {
//            log.warn("authorized_keys file not found.");
//            return new ArrayList<>();
//        }
//        List<PublicKey> publicKeys = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(authorizedKeys))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                if (!line.isEmpty() && !line.startsWith("#")) {
//                    String[] parts = line.split(" ");
//                    if (parts.length > 1 && parts[0].equals("ssh-rsa")) {
//                        PublicKey publicKey = parseOpenSSHKey(line);
//                        if (publicKey != null) {
//                            publicKeys.add(publicKey);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("Error loading public keys from authorized_keys file", e);
//            throw e;
//        }
//        return publicKeys;
//    }
//
//    public PublicKey parseOpenSSHKey(String decodedKey) {
//        try {
//            // Use BouncyCastle to parse the OpenSSH key
//            PEMParser pemParser = new PEMParser(new StringReader(decodedKey));
//            Object pemObject = pemParser.readObject();
//
//            if (pemObject == null) {
//                log.error("Failed to read PEM object from decoded key.");
//                return null;
//            }
//
//            if (pemObject instanceof SubjectPublicKeyInfo) {
//                SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) pemObject;
//                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
//                return converter.getPublicKey(publicKeyInfo);
//            } else {
//                log.error("Unexpected PEM object type: {}", pemObject.getClass().getName());
//                return null;
//            }
//        } catch (Exception e) {
//            log.error("Error parsing OpenSSH key: ", e);
//            return null;
//        }
//    }
}