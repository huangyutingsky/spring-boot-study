package org.example.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/23 15:28
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogInfo {
    @XmlElement(name = "catalogID")
    private String catalogID;

    @XmlElement(name = "catalogName")
    private String catalogName;
}
