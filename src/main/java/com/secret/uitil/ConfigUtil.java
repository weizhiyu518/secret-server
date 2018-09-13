package com.secret.uitil;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * @author weizhiyu
 * @create 2018-09-13 下午1:15
 */
public class ConfigUtil {

    private static final Map<String, CertificateConfigBean> CERTIFICATE_CONFIG = Maps.newHashMap();

    static {

        CERTIFICATE_CONFIG.put("1", new CertificateConfigBean("1", "www.zlex.org","123456","/Users/weizhiyu/zlex.keystore", "/Users/weizhiyu/zlex.cer"));
    }


    public static CertificateConfigBean getCertificateConfig(String clientId) {
        return CERTIFICATE_CONFIG.get(clientId);
    }

    @Data
    public static class CertificateConfigBean {
        private String clientId;
        private String alias;
        private String password;
        private String keyStorePath;
        private String certificatePath;

        public CertificateConfigBean(String clientId,String alias,String password, String keyStorePath, String certificatePath) {
            this.clientId = clientId;
            this.alias=alias;
            this.password=password;
            this.keyStorePath = keyStorePath;
            this.certificatePath = certificatePath;
        }
    }
}
