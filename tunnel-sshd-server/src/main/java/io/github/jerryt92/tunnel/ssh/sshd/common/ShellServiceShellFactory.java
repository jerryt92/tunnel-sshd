package io.github.jerryt92.tunnel.ssh.sshd.common;

import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

public class ShellServiceShellFactory implements ShellFactory {

    @Override
    public Command createShell(ChannelSession channelSession) {
        return new BaseCommand();
    }
}