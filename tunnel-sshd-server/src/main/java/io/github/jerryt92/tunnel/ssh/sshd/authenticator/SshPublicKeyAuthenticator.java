package io.github.jerryt92.tunnel.ssh.sshd.authenticator;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.common.config.keys.PublicKeyEntryResolver;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class SshPublicKeyAuthenticator implements PublickeyAuthenticator {
    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession session) {
        try {
            HashSet<PublicKey> publicKeys = loadPublicKeys();
            return publicKeys.contains(key);
        } catch (Exception e) {
            log.error("Error loading public keys.", e);
        }
        return false;
    }

    private HashSet<PublicKey> loadPublicKeys() throws Exception {
        ClassLoader classLoader = PasswordAuthenticator.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("authorized_keys")).getFile());
        Path path = file.toPath();
        List<AuthorizedKeyEntry> keys = AuthorizedKeyEntry.readAuthorizedKeys(Objects.requireNonNull(path));
        HashSet<PublicKey> publicKeys = new HashSet<>();
        if (GenericUtils.isEmpty(keys)) {
            return publicKeys;
        }
        for (AuthorizedKeyEntry key : keys) {
            publicKeys.add(key.resolvePublicKey(null, PublicKeyEntryResolver.IGNORING));
        }
        return publicKeys;
    }
}
