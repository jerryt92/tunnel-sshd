package io.github.jerryt92.tunnel.ssh.sshd.util.net.ip;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * @author tianjingli
 * @since 2024-01-23 <br/> 参考 {@link io.github.jerryt92.tunnel.ssh.sshd.util.net.ip.IpAddress} or {@link io.github.jerryt92.tunnel.ssh.sshd.util.net.ip.IpPrefix}
 */
public class IPAddressUtil {
    /**
     * 判断是否为合法IPv4/掩码地址或IPv6/前缀地址
     */
    public static Boolean isIPSubnet(String iPSubnet) {
        try {
            IpPrefix.valueOf(iPSubnet);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 是否为合法IPv4地址
     *
     * @param var0
     * @return
     */
    public static Boolean isIPv4LiteralAddress(String var0) {
        try {
            return IpAddress.valueOf(var0).isIp4();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 是否为合法IPv6地址
     *
     * @param var0
     * @return
     */
    public static Boolean isIPv6LiteralAddress(String var0) {
        try {
            return IpAddress.valueOf(var0).isIp6();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 将IPv4地址转换为字节数组
     *
     * @param var0
     * @return
     */
    public static byte[] textToNumericFormatV4(String var0) {
        try {
            IpAddress ip = IpAddress.valueOf(var0);
            if (ip.isIp4()) {
                return ip.toOctets();
            } else {
                return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 将IPv6地址转换为字节数组
     *
     * @param var0
     * @return
     */
    public static byte[] textToNumericFormatV6(String var0) {
        try {
            IpAddress ip = IpAddress.valueOf(var0);
            if (ip.isIp6()) {
                return ip.toOctets();
            } else {
                return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 将字节数组转换为IPv4地址
     *
     * @param var0
     * @return
     */
    public static String numericFormatV4toText(byte[] var0) {
        try {
            return IpAddress.valueOf(IpAddress.Version.INET, var0).toString();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 将字节数组转换为IPv6地址
     *
     * @param var0
     * @return
     */
    public static String numericFormatV6toText(byte[] var0) {
        try {
            return IpAddress.valueOf(IpAddress.Version.INET6, var0).toString();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 将IPv6地址转换为缩写形式
     *
     * @param ipv6Text
     * @return
     */
    public static String compressIPv6(String ipv6Text) {
        return Ip6Address.valueOf(ipv6Text).toString();
    }

    /**
     * 将IPv4地址转换为IPv6的IPv4映射地址
     *
     * @param var0
     * @return
     */
    public static byte[] convertFromIPv4MappedAddress(byte[] var0) {
        if (isIPv4MappedAddress(var0)) {
            byte[] var1 = new byte[4];
            System.arraycopy(var0, 12, var1, 0, 4);
            return var1;
        } else {
            return null;
        }
    }

    /**
     * 判断IPv6地址是否为IPv4映射地址，即::ffff:0:0/96
     *
     * @param var0
     * @return
     */
    private static Boolean isIPv4MappedAddress(byte[] var0) {
        if (var0.length < 16) {
            return false;
        } else {
            return var0[0] == 0 && var0[1] == 0 && var0[2] == 0 && var0[3] == 0 && var0[4] == 0 && var0[5] == 0 && var0[6] == 0 && var0[7] == 0 && var0[8] == 0 && var0[9] == 0 && var0[10] == -1 && var0[11] == -1;
        }
    }

    /**
     * 比较起始IP是否小于结束IP
     *
     * @param beginIP
     * @param endIP
     * @return
     */
    public static Boolean isBeginLessThanEnd(String beginIP, String endIP) {
        IpAddress ip1 = IpAddress.valueOf(beginIP);
        IpAddress ip2 = IpAddress.valueOf(endIP);
        return ip1.compareTo(ip2) < 0;
    }

    /**
     * 计算IPv4地址数量
     *
     * @param beginIP
     * @param endIP
     * @return
     */
    public static Long calculateIPv4Count(String beginIP, String endIP) {
        try {
            Ip4Address ip1 = Ip4Address.valueOf(beginIP);
            Ip4Address ip2 = Ip4Address.valueOf(endIP);
            return (long) (Math.max(0, ip2.toInt() - ip1.toInt() + 1));
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    /**
     * 计算IPv4地址数量
     *
     * @param iPSegment eg: 192.168.0.0/16、192.168.1.0-192.168.2.0
     * @return
     */
    public static Long calculateIPv4Count(String iPSegment) {
        iPSegment = trim(iPSegment);
        if (iPSegment.contains("/")) {
            try {
                Ip4Prefix prefix = Ip4Prefix.valueOf(iPSegment);
                // 排除掉网络地址和广播地址
                return (long) Math.pow(2, IpAddress.INET_BIT_LENGTH - prefix.prefixLength()) - 2;
            } catch (IllegalArgumentException e) {
                return 0L;
            }
        }
        if (iPSegment.contains("-")) {
            return calculateIPv4Count(iPSegment.split("-")[0], iPSegment.split("-")[1]);
        }
        return 0L;
    }

    /**
     * 计算IPv6地址数量
     *
     * @param beginIP
     * @param endIP
     * @return
     */
    public static BigInteger calculateIPv6Count(String beginIP, String endIP) {
        try {
            Ip6Address ip1 = Ip6Address.valueOf(beginIP);
            Ip6Address ip2 = Ip6Address.valueOf(endIP);
            BigInteger ip1Int = new BigInteger(ip1.toOctets());
            BigInteger ip2Int = new BigInteger(ip2.toOctets());
            BigInteger result = ip2Int.subtract(ip1Int).add(BigInteger.ONE);
            return result.compareTo(BigInteger.ZERO) < 0 ? BigInteger.ZERO : result;
        } catch (IllegalArgumentException e) {
            return BigInteger.ZERO;
        }
    }

    /**
     * 计算IPv6地址数量
     *
     * @param iPSegment eg: 2001:db8:0:0:0:0:0:0/32、2001:db8:0:0:0:0:0:0-2001:db8:0:0:0:0:0:1
     * @return
     */
    public static BigInteger calculateIPv6Count(String iPSegment) {
        iPSegment = trim(iPSegment);
        if (iPSegment.contains("/")) {
            try {
                Ip6Prefix prefix = Ip6Prefix.valueOf(iPSegment);
                // TODO: IPv6地址是否排除掉特殊地址？
                return BigInteger.valueOf(2).pow(IpAddress.INET6_BIT_LENGTH - prefix.prefixLength());
            } catch (IllegalArgumentException e) {
                return BigInteger.ZERO;
            }
        }
        if (iPSegment.contains("-")) {
            return calculateIPv6Count(iPSegment.split("-")[0], iPSegment.split("-")[1]);
        }
        return BigInteger.ZERO;
    }

    /**
     * 解析IPv4地址段
     *
     * @param iPSegment
     * @return
     */
    public static List<String> parseIPv4Segment(String iPSegment) {
        List<String> ipList = new ArrayList<>();
        asyncParseIPv4Segment(iPSegment, ipList::add);
        return ipList;
    }

    /**
     * 解析IPv4地址段（结果异步放入集合）
     *
     * @param iPSegment
     * @param ipList    线程安全的集合
     */
    public static void asyncParseIPv4Segment(String iPSegment, ConcurrentLinkedQueue<String> ipList) {
        asyncParseIPv4Segment(iPSegment, ipList::add);
    }

    /**
     * 解析IPv4地址段（结果异步处理）
     *
     * @param iPSegment
     * @param action    处理结果的函数
     */
    public static void asyncParseIPv4Segment(String iPSegment, Consumer<String> action) {
        iPSegment = trim(iPSegment);
        if (iPSegment.contains("/")) {
            Ip4Prefix prefix;
            try {
                prefix = Ip4Prefix.valueOf(iPSegment);
            } catch (IllegalArgumentException e) {
                return;
            }
            int addressInt = prefix.address().toInt();
            long c = (long) Math.pow(2, IpAddress.INET_BIT_LENGTH - prefix.prefixLength()) - 2;
            for (long i = 0; i < c; i++) {
                addressInt += 1;
                action.accept(Ip4Address.valueOf(addressInt).toString());
            }
        } else if (iPSegment.contains("-")) {
            String beginIP = iPSegment.split("-")[0];
            String endIP = iPSegment.split("-")[1];
            try {
                int ip1 = Ip4Address.valueOf(beginIP).toInt();
                int ip2 = Ip4Address.valueOf(endIP).toInt();
                for (int i = ip1; i <= ip2; i++) {
                    action.accept(Ip4Address.valueOf(i).toString());
                }
            } catch (IllegalArgumentException e) {
                return;
            }
        }
    }

    public static List<String> parseIPv6Segment(String iPSegment) {
        List<String> ipList = new ArrayList<>();
        asyncParseIPv6Segment(iPSegment, ipList::add);
        return ipList;
    }

    public static void asyncParseIPv6Segment(String iPSegment, Consumer<String> action) {
        iPSegment = trim(iPSegment);
        if (iPSegment.contains("/")) {
            Ip6Prefix prefix;
            try {
                prefix = Ip6Prefix.valueOf(iPSegment);
            } catch (IllegalArgumentException e) {
                return;
            }
            BigInteger addressInt = new BigInteger(1, prefix.address().toOctets());
            BigInteger c = BigInteger.valueOf(2).pow(IpAddress.INET6_BIT_LENGTH - prefix.prefixLength());
            for (BigInteger i = BigInteger.ZERO; i.compareTo(c) < 0; i = i.add(BigInteger.ONE)) {
                addressInt = addressInt.add(BigInteger.ONE);
                action.accept(bigIntegerToIp6Address(addressInt).toString());
            }
        } else if (iPSegment.contains("-")) {
            String beginIP = iPSegment.split("-")[0];
            String endIP = iPSegment.split("-")[1];
            try {
                BigInteger ip1 = new BigInteger(1, Ip6Address.valueOf(beginIP).toOctets());
                BigInteger ip2 = new BigInteger(1, Ip6Address.valueOf(endIP).toOctets());
                for (BigInteger i = ip1; i.compareTo(ip2) <= 0; i = i.add(BigInteger.ONE)) {
                    action.accept(bigIntegerToIp6Address(i).toString());
                }
            } catch (IllegalArgumentException e) {
                return;
            }
        }
    }

    private static Ip6Address bigIntegerToIp6Address(BigInteger val) {
        byte[] origin = val.toByteArray();
        byte[] result = new byte[IpAddress.INET6_BYTE_LENGTH];
        System.arraycopy(origin, 0, result, IpAddress.INET6_BYTE_LENGTH - origin.length, origin.length);
        return Ip6Address.valueOf(result);
    }

    private static String trim(String str) {
        return str == null ? null : str.trim();
    }
}
