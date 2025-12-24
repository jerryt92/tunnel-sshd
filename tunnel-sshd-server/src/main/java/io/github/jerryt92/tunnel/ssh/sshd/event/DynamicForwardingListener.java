package io.github.jerryt92.tunnel.ssh.sshd.event;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.channel.ChannelListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DynamicForwardingListener implements ChannelListener {
    @Override
    public void channelInitialized(Channel channel) {
        ChannelListener.super.channelInitialized(channel);
    }

    @Override
    public void channelOpenSuccess(Channel channel) {
        ChannelListener.super.channelOpenSuccess(channel);
    }

    @Override
    public void channelOpenFailure(Channel channel, Throwable reason) {
        ChannelListener.super.channelOpenFailure(channel, reason);
    }

    @Override
    public void channelStateChanged(Channel channel, String hint) {
        ChannelListener.super.channelStateChanged(channel, hint);
    }

    @Override
    public void channelClosed(Channel channel, Throwable reason) {
        ChannelListener.super.channelClosed(channel, reason);
    }
}
