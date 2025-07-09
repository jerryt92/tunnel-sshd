package io.github.jerryt92.tunnel.ssh.sshd.common;

import java.util.Objects;
import java.util.Scanner;

import io.github.jerryt92.tunnel.ssh.sshd.service.SshSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShellService {
    private static ShellService instance;
    @Autowired
    private SshSessionService sshSessionService;

    public ShellService() {
        instance = this;
    }

    public void start() {
        ShellService var10002 = instance;
        Objects.requireNonNull(var10002);
        (new Thread(var10002::startCommandLineInterface)).start();
    }

    private void startCommandLineInterface() {
        System.out.println();
        printHelp();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim();
            if (command.equals("close")) {
                System.out.println("Please specify the index of the session to close.");
                System.out.println("Showing all ssh sessions...");
                this.sshSessionService.showSshSessions();
            } else if (command.equals("help")) {
                printHelp();
            } else {
                if (command.equals("exit")) {
                    System.out.println("Exiting...");
                    System.exit(0);
                    return;
                }

                if (command.startsWith("close")) {
                    String[] parts = command.split(" ");
                    if (parts.length != 2) {
                        System.out.println("Invalid command. Usage: close <index>");
                    } else {
                        try {
                            int index = Integer.parseInt(parts[1]);
                            this.sshSessionService.closeSessionByIndex(index);
                        } catch (NumberFormatException var5) {
                            System.out.println("Invalid index");
                        }
                    }
                } else if (command.equals("show")) {
                    this.sshSessionService.showSshSessions();
                } else {
                    System.out.println("未知命令，输入“help”查看帮助信息");
                }
            }
        }
    }

    private static void printHelp() {
        System.out.println("可用命令：");
        System.out.println("  help         - 显示帮助信息");
        System.out.println("  exit         - 关闭程序");
        System.out.println("  show         - 显示所有SSH会话");
        System.out.println("  close <索引>  - 关闭指定索引的SSH会话");
    }
}
