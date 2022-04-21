package org.example.entity;

import lombok.*;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/23 14:16
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "result")
public class CaiyunCreateCatalogExtResponseEntity{

    @XmlAttribute(name = "resultCode")
    private String resultCode;

    @XmlElement(name = "catalogInfo")
    private CatalogInfo catalogInfo;

}
