package test;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONObject;
import com.secret.uitil.AESUtil;
import com.secret.uitil.ConfigUtil;
import com.secret.uitil.HttpClientUtil;

import certificate.one.CertificateCoder;

/**
 * @author weizhiyu
 * @create 2018-09-13 上午11:43
 */
public class ClientTest {

    public static void main(String[] args) throws Exception {
        String clientId = "1";
        String serverId = "1";
        String transId = RandomStringUtils.randomNumeric(16);
        String transData = RandomStringUtils.random(10);
        ConfigUtil.CertificateConfigBean certificateConfigBean = ConfigUtil.getCertificateConfig(clientId);
        String aesKey = AESUtil.generateAESKey();

        // 公钥加密
        byte[] encrypt = CertificateCoder.encryptByPublicKey(aesKey.getBytes(),
                certificateConfigBean.getCertificatePath());
        String sign = Base64Utils.encodeToString(encrypt);
        System.out.println(sign);
        String secretBefore = RandomStringUtils.random(10, "UTF-8");
        String secret = AESUtil.encrypt(secretBefore, aesKey, transId);
        JSONObject header = new JSONObject();
        header.put("clientId", clientId);
        header.put("serviceId", serverId);
        JSONObject body = new JSONObject();
        body.put("transId", transId);
        body.put("transData", transData);
        JSONObject request = new JSONObject();
        request.put("header", header);
        request.put("body", body);
        request.put("secret", secret);
        JSONObject param = new JSONObject();
        param.put("request", request);
        param.put("sign", sign);
        String resultJson = HttpClientUtil.postJson("http://127.0.0.1:8080/check", param.toJSONString());
        System.out.println(resultJson);
        JSONObject result = JSONObject.parseObject(resultJson);
        String retSign = result.getString("sign");
        JSONObject response = result.getJSONObject("response");
        JSONObject retBody = response.getJSONObject("body");
        String retTransId = retBody.getString("retTransId");
        String retTransData = retBody.getString("retTransData");
        String retAesKey = new String(CertificateCoder.decryptByPublicKey(Base64Utils.decodeFromString(retSign),
                certificateConfigBean.getCertificatePath()), "UTF-8");
        System.out.println(retAesKey);
        String retTransDataDecrypt = AESUtil.decrypt(retTransData, retAesKey, retTransId);
        System.out.println(retTransDataDecrypt);
    }

}
