package io.github.jerryt92.tunnel.ssh.sshd.common;

import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BaseCommand implements Command {
    private OutputStream outputStream;
    private InputStream inputStream;
    private OutputStream errorStream;
    private ExitCallback exitCallback;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void setErrorStream(OutputStream errorStream) {
        this.errorStream = errorStream;
    }

    @Override
    public void setExitCallback(ExitCallback exitCallback) {
        this.exitCallback = exitCallback;
    }

    @Override
    public void start(ChannelSession channelSession, Environment environment) throws IOException {
        // 在单独线程中运行，避免阻塞SSH连接
        executor.submit(() -> {
            try {
                outputStream.write("Welcome to Tunnel SSH Server!\n".getBytes());
                outputStream.flush();
                latch.await();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Exiting...");
        });
    }

    @Override
    public void destroy(ChannelSession channelSession) {
        latch.countDown();
        executor.shutdown();
    }
}