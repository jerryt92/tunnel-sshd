
package io.github.jerryt92.tunnel.ssh.sshd.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Ed25519工具类，包含密钥生成、签名和验签的功能
 *
 * @author jerryt92.github.io
 * @date 2023/10/10
 */
public class Ed25519Util {
    static {
        // 注册 Bouncy Castle 提供者
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 数字签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "Ed25519";

    /**
     * 生成Ed25519公钥/私钥对
     *
     * @return keyPair
     */
    public static KeyPair generateEd25519KeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("Ed25519");
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 把公钥转换为byte[]
     *
     * @param publicKey
     * @return
     */
    public static byte[] getPublicKeyBytes(PublicKey publicKey) {
        return publicKey.getEncoded();
    }

    /**
     * 把byte[]转换为公钥
     *
     * @param publicKeyBytes
     * @return
     */
    public static PublicKey getPublicKeyFromBytes(byte[] publicKeyBytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519", "BC");
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }

    /**
     * 把私钥转换为byte[]
     *
     * @param privateKey
     * @return
     */
    public static byte[] getPrivateKeyBytes(PrivateKey privateKey) {
        return privateKey.getEncoded();
    }

    /**
     * 把byte[]转换为私钥
     *
     * @param privateKeyBytes
     * @return
     */
    public static PrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        return privateKey;
    }

    /**
     * 签名
     *
     * @param privateKeyStr 私钥
     * @param data          签名内容
     * @return signature 签名值
     */
    public static String sign(String privateKeyStr, String data)
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException {
        PrivateKey privateKey = getPrivateKeyFromBytes(Base64.getDecoder().decode(privateKeyStr));
        Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
        sign.initSign(privateKey);
        sign.update(data.getBytes());
        String signature = Base64.getEncoder().encodeToString(sign.sign());
        return signature;
    }

    /**
     * 验签
     *
     * @param publicKeyStr 公钥
     * @param data         验签内容
     * @param signature    签名值
     * @return 验签结果
     */
    public static boolean verifySign(String publicKeyStr, String data, String signature)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, NoSuchProviderException {
        PublicKey publicKey = getPublicKeyFromBytes(Base64.getDecoder().decode(publicKeyStr));
        Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
        verifySign.initVerify(publicKey);
        verifySign.update(data.getBytes());
        return verifySign.verify(Base64.getDecoder().decode(signature));
    }

    public static void main(String[] args) throws Exception {
        // 数字签名演示
        System.out.println("\n===========数字签名演示===========\n");
        String msg = "Hello Ed25519.";
        System.out.println("原文：" + msg);
        // 获取公钥/私钥对
        KeyPair keyPair = generateEd25519KeyPair();
        // 将公钥/私钥转为可明文显示的字符串
        String publicKeyStr = Base64.getEncoder().encodeToString(getPublicKeyBytes(keyPair.getPublic()));
        String privateKeyStr = Base64.getEncoder().encodeToString(getPrivateKeyBytes(keyPair.getPrivate()));
        // 明文显示公钥/私钥
        System.out.println("PublicKey : " + publicKeyStr);
        System.out.println("PrivateKey : " + privateKeyStr);
        String signature = sign(privateKeyStr, msg);
        System.out.println("签名值：" + signature);
        System.out.println("验签结果：" + verifySign(publicKeyStr, msg, signature));
    }
}