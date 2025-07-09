package io.github.jerryt92.tunnel.ssh.sshd.crypto;

/**
 * 使用固定的RSA密钥对，以免每次启动都生成新的密钥对
 * Base64编码的RSA公钥和私钥
 */
public interface Ed25519KeyPair {
    String PUBLIC_KEY = "MCowBQYDK2VwAyEAuWMrNW+JlXriCt40Zw4R1UGvywXhPJyxrVhv4OV+1pM=";
    String PRIVATE_KEY = "MFECAQEwBQYDK2VwBCIEIHuO4swhf/tTzoKQPuB3lRXclACnZEpUTs3WeGJ1E1gjgSEAuWMrNW+JlXriCt40Zw4R1UGvywXhPJyxrVhv4OV+1pM=";
}
