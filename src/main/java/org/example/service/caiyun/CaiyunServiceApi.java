package org.example.service.caiyun;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * @author huangyuting
 * @Description: 彩云服务
 * @date 2021/11/22 11:41
 */
@Slf4j
@Getter
@Component
public class CaiyunServiceApi {
    private static final int INIT_KEY_LENGTH = 16;

    @Value("10216500")
    private String channelSrc;
    @Value("aes-128-cbc")
    private String aesName;
    @Value("MODULE46DA041C44")
    private String aesKey;
    @Value("YZHospital")
    private String neName;
    @Value("YZHospital#uRYAP+;Tc!wn7%ePWP8*X81b_-t!g@")
    private String nePass;
    @Value("HC000")
    private String hospitalCode;


    @Autowired
    protected RestTemplate restTemplate;

    /**
     * 鉴权方式：NE-Auth
     *
     * @return String
     */
    protected String getXneAuth() {
        String input = neName + ":" + nePass;
        byte[] initKey = aesKey.getBytes();

        if (input == null || "".equals(input) || initKey == null || initKey.length != INIT_KEY_LENGTH) {
            return null;
        }

        // 生成随机向量
        byte[] iv = new byte[INIT_KEY_LENGTH];
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(iv);

        SecretKeySpec skeySpec = new SecretKeySpec(initKey, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
            byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8.name()));

            // 加密完成后，将随机向量添加到密文前面
            byte[] cipherTextWithIv = new byte[cipherText.length + INIT_KEY_LENGTH];
            System.arraycopy(iv, 0, cipherTextWithIv, 0, INIT_KEY_LENGTH);
            System.arraycopy(cipherText, 0, cipherTextWithIv, INIT_KEY_LENGTH, cipherText.length);
            return Base64.encode(cipherTextWithIv);
        } catch (Exception e) {
            log.error("CaiyunServiceApi:getXneAuth方法异常:{}", e.getMessage(), e);
            return null;
        }
    }


}