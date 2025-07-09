package io.github.jerryt92.tunnel.ssh.sshd.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NeHttpProxyCommandDto {
    private String neId;
    private String categoryId;
    private Integer neTypeId;
    private Integer neModelId;
}
