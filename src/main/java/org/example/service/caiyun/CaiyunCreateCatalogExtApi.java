package org.example.service.caiyun;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author huangyuting
 * @Description: 和彩云根据父目录ID创建网盘子目录
 * @date 2022/1/11 15:07
 */
@Slf4j
@Component
public class CaiyunCreateCatalogExtApi extends CaiyunServiceApi {

    private final String CREATE_CATALOG_URL = "https://ose.caiyun.feixin.10086.cn/richlifeApp/devapp/ICatalog";

    private static final String REQ_XML_FORMAT =
            "<createCatalogExt>\n" +
            "    <createCatalogExtReq>\n" +
            "        <parentCatalogID>%s</parentCatalogID>\n" +
            "        <newCatalogName>%s</newCatalogName>\n" +
            "        <ownerMSISDN>%s</ownerMSISDN>\n" +
            "        <catalogType>%d</catalogType>\n" +
            "    </createCatalogExtReq>\n" +
            "</createCatalogExt>";

    @Value("00019700101000000239")
    private String mrCatalogParentId;

    /**
     * 和彩云根据父目录ID创建网盘子目录
     *
     * @param newCatalogName 子目录名称
     * @param mobile 手机号
     * @param catalogType 子目录类型
     * @return
     */
    public ResponseEntity<String> createCatalogExt(String newCatalogName, String mobile, Integer catalogType) throws UnsupportedEncodingException {
        long startTime = System.currentTimeMillis();

        String reqXml = String.format(REQ_XML_FORMAT, mrCatalogParentId, newCatalogName, mobile, catalogType);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/xml;charset=utf-8");
        headers.set("x-huawei-channelSrc", super.getChannelSrc());
        headers.set("x-ne-auth", super.getXneAuth());
        HttpEntity<String> reqEntity = new HttpEntity<>(reqXml, headers);

        log.info("和彩云创建网盘子目录接口请求参数: newCatalogName={} mobile={} catalogType={}", newCatalogName, mobile, catalogType);
        log.debug("和彩云创建网盘子目录请求参数:{}", reqXml);

        ResponseEntity<String> responseEntity = restTemplate.exchange(CREATE_CATALOG_URL, HttpMethod.POST, reqEntity, String.class);

        log.info("和彩云创建网盘子目录接口响应参数:{}, 请求参数: newCatalogName={} mobile={} catalogType={}, 耗时:{} ms", responseEntity.getBody(), newCatalogName, mobile, catalogType, System.currentTimeMillis() - startTime);
        return responseEntity;
    }

}
