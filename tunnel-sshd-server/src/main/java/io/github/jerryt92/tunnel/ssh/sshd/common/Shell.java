package io.github.jerryt92.tunnel.ssh.sshd.common;

import java.util.Objects;
import java.util.Scanner;

import io.github.jerryt92.tunnel.ssh.sshd.service.SshSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Shell {
    private static Shell instance;
    @Autowired
    private SshSessionService sshSessionService;

    public Shell() {
        instance = this;
    }

    public void start() {
        Shell var10002 = instance;
        Objects.requireNonNull(var10002);
        (new Thread(var10002::startCommandLineInterface)).start();
    }

    private void startCommandLineInterface() {
        System.out.println("Starting command line interface...");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            System.out.println();
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
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
                }
            }
        }
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  help     - Show this help message");
        System.out.println("  exit     - Exit the application");
        System.out.println("  show - Show all SSH sessions");
        System.out.println("  close <index> - Close the SSH session with the specified index");
    }
}
