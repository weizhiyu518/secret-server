package com.secret.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.secret.uitil.AESUtil;
import com.secret.uitil.CertificateCoderUtil;
import com.secret.uitil.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author weizhiyu
 * @create 2018-09-13 上午10:58
 */
@RestController()
@Slf4j
public class SecretController {

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public Object ping(HttpServletRequest request) {

        Map<String, Object> result = Maps.newHashMap();
        result.put("code", "0000");
        return result;
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public Object check(@RequestBody JSONObject param) {
        String retCode = "";
        String retMsg = "";
        String retTransId = "";
        String retTransData = "";
        String retSign = "";
        try {
            JSONObject request = param.getJSONObject("request");
            String sign = param.getString("sign");
            JSONObject header = request.getJSONObject("header");
            JSONObject body = request.getJSONObject("body");
            String secret = request.getString("secret");
            String clientId = header.getString("clientId");
            String serverId = header.getString("serverId");
            String transId = body.getString("transId");
            String transData = body.getString("transData");
            ConfigUtil.CertificateConfigBean certificateConfigBean = ConfigUtil.getCertificateConfig(clientId);
            log.info("check param:{}", param);
            //解密
            String aesKey = new String(CertificateCoderUtil.decryptByPrivateKey(Base64Utils.decodeFromString(sign),
                    certificateConfigBean.getKeyStorePath(), certificateConfigBean.getAlias(),
                    certificateConfigBean.getPassword()), "UTF-8");
            String secretBefore = AESUtil.decrypt(secret, aesKey, transId);
            String retAesKey = AESUtil.generateAESKey();
            retSign = Base64Utils.encodeToString(CertificateCoderUtil.encryptByPrivateKey(retAesKey.getBytes(),
                    certificateConfigBean.getKeyStorePath(), certificateConfigBean.getAlias(),
                    certificateConfigBean.getPassword()));
            retTransData = AESUtil.encrypt(secretBefore, retAesKey, transId);
            retTransId = transId;
            retCode = "0";
            retMsg = "SUCCESS";

        } catch (Exception e) {
            log.error("check error param:{}", param, e);
            retCode = "1";
            retMsg = "ERROR";
        }
        JSONObject retHeader = new JSONObject();
        retHeader.put("retCode", retCode);
        retHeader.put("retMsg", retMsg);
        JSONObject retBody = new JSONObject();
        retBody.put("retTransId", retTransId);
        retBody.put("retTransData", retTransData);
        JSONObject response = new JSONObject();
        response.put("header", retHeader);
        response.put("body", retBody);
        JSONObject result = new JSONObject();
        result.put("response", response);
        result.put("sign", retSign);
        return result;
    }
}
