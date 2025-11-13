package com.org.share_recycled_stuff.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vnpay")
@Getter
@Setter
public class VNPayConfig {
    private String vnpTmnCode;

    private String vnpHashSecret;

    private String vnpUrl;

    private String vnpReturnUrl;

    private String vnpIpnUrl;
}
