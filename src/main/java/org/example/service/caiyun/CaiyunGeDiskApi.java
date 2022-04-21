package org.example.service.caiyun;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author huangyuting
 * @Description: 和彩云根据手机号查询用户ID（弃用）
 * @date 2022/1/11 15:07
 */
@Deprecated
@Slf4j
@Component
public class CaiyunGeDiskApi extends CaiyunServiceApi {
    private static final String REQ_XML_FORMAT = "<getDisk>\n" +
            "  <MSISDN>15302497202</MSISDN>\n" +
            "  <catalogID>00019700101000000332</catalogID>\n" +
            "  <entryShareCatalogID></entryShareCatalogID>\n" +
            "  <filterType>0</filterType>\n" +
            "  <catalogSortType>0</catalogSortType>\n" +
            "  <contentType>0</contentType>\n" +
            "  <contentSortType>0</contentSortType>\n" +
            "  <sortDirection>0</sortDirection>\n" +
            "  <startNumber>40</startNumber>\n" +
            "  <endNumber>50</endNumber>\n" +
            "  <channelList>10000000</channelList>\n" +
            "  <catalogType>0</catalogType>\n" +
            "</getDisk>";

    /**
     * 和彩云查询用户ID
     *
     * @param mobile:用户手机
     * @return ResponseEntity
     * huangyutingt ResponseEntity的body要封装成对应的实体对象，并返回，不能返回ResponseEntity
     */
    public ResponseEntity<String> getGeDisk(String mobile) {
        long startTime = System.currentTimeMillis();
        String reqUrl = "http://218.2.129.52:2393" + "/richlifeApp/devapp/ICatalog";
//        String reqXml = String.format(REQ_XML_FORMAT, mobile);
        String reqXml = REQ_XML_FORMAT;
                HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/xml;UTF-8");
        headers.set("x-huawei-channelSrc", super.getChannelSrc());
        headers.set("x-ne-auth", super.getXneAuth());
        HttpEntity<String> reqEntity = new HttpEntity<>(reqXml, headers);

//        log.info("和彩云查询用户ID接口请求参数:mobile:{}", mobile);
        log.info("和彩云查询用户ID接口请求参数:{}", reqXml);

        ResponseEntity<String> getUserIdResponseEntity = restTemplate.exchange(reqUrl, HttpMethod.POST, reqEntity, String.class);

        log.info("和彩云查询用户ID接口响应参数:{}, 请求参数:{}, 耗时:{} ms", getUserIdResponseEntity.getBody(), mobile, System.currentTimeMillis() - startTime);
        return getUserIdResponseEntity;
    }

}
