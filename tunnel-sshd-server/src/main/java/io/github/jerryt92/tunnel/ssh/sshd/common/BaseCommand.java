package io.github.jerryt92.tunnel.ssh.sshd.common;

import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BaseCommand implements Command {
    private OutputStream outputStream;
    private InputStream inputStream;
    private OutputStream errorStream;
    private ExitCallback exitCallback;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
    public void start(ChannelSession channelSession, Environment environment) {
        String username = environment.getEnv().get("USER");
        executor.submit(() -> {
            try {
                write("Hello, " + username + "! Welcome to Tunnel SSH Server!\r\n");
                write("Type 'help' for commands.\r\n");
                write("> "); // 打印初始提示符

                StringBuilder cmdBuffer = new StringBuilder();
                int c;
                // 逐字节读取输入流
                while ((c = inputStream.read()) != -1) {

                    // --- 1. 处理回车换行 (Enter) ---
                    if (c == '\r') {
                        // 用户按了回车，服务器要回显换行
                        write("\r\n");

                        // 获取命令并执行
                        String cmd = cmdBuffer.toString().trim();
                        if (!cmd.isEmpty()) {
                            boolean shouldExit = processCommand(cmd);
                            if (shouldExit) {
                                break;
                            }
                        }

                        // 清空缓冲区，打印新的提示符
                        cmdBuffer.setLength(0);
                        write("> ");
                    }
                    // --- 2. 处理退格键 (Backspace / Delete) ---
                    // 127 是 DEL, 8 是 Backspace，不同客户端可能不同
                    else if (c == 127 || c == 8) {
                        if (cmdBuffer.length() > 0) {
                            // 从缓冲区移除最后一个字符
                            cmdBuffer.deleteCharAt(cmdBuffer.length() - 1);
                            // 视觉上的退格：输出 "退格-空格-退格"
                            // 这会让光标回退，用空格覆盖字符，再回退
                            outputStream.write(new byte[]{'\b', ' ', '\b'});
                            outputStream.flush();
                        }
                    }
                    // --- 3. 处理普通字符 ---
                    else {
                        // 累加到命令缓冲区
                        cmdBuffer.append((char) c);
                        // 关键：必须回显字符，用户才能看见
                        outputStream.write(c);
                        outputStream.flush();
                    }
                }
            } catch (IOException e) {
                // Ignore disconnect
            } finally {
                if (exitCallback != null) {
                    exitCallback.onExit(0);
                }
            }
        });
    }

    /**
     * 处理具体命令逻辑
     *
     * @return true 表示需要退出连接
     */
    private boolean processCommand(String cmd) throws IOException {
        if ("exit".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)) {
            write("Bye!\r\n");
            return true;
        } else if ("help".equalsIgnoreCase(cmd)) {
            printHelp();
        } else {
            write("Unknown command: " + cmd + "\r\n");
            printHelp();
        }
        return false;
    }

    private void write(String msg) throws IOException {
        outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    @Override
    public void destroy(ChannelSession channelSession) {
        executor.shutdownNow();
    }

    private void printHelp() throws IOException {
        write("Available commands:\r\n");
        write("  help  - Show this help message\r\n");
        write("  exit  - Close the connection\r\n");
    }
}