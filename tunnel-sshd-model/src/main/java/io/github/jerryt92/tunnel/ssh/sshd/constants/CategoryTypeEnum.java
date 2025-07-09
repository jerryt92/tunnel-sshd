package io.github.jerryt92.tunnel.ssh.sshd.constants;

/**
 * category type。<br> 0：网元<br> 1：子网<br> 2：纯符号<br> 3：链路<br> 4：卡<br> 5：插槽<br> 6：端口<br> 7：机箱
 */
public enum CategoryTypeEnum {
    NE(0),

    SUBNET(1),

    SYMBOL(2),

    LINK(3),

    CARD(4),

    SLOT(5),

    PORT(6),

    CHASSIS(7);

    private Integer value;

    CategoryTypeEnum(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static CategoryTypeEnum fromValue(Integer value) {
        for (CategoryTypeEnum b : CategoryTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
