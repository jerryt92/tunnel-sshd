package io.github.jerryt92.tunnel.ssh.sshd.spi;

import io.github.jerryt92.tunnel.ssh.sshd.model.NeHttpProxyCommandDto;

public interface INeHttpProxyCommand {
    NeHttpProxyCommandDto checkDeleteResource(NeHttpProxyCommandDto dto);
}
