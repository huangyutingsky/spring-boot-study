package org.example.entity;

import lombok.Data;

import javax.xml.bind.annotation.*;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/23 15:18
 */
@Data
// 控制JAXB 绑定类中属性和字段的排序
@XmlType(propOrder = {
        "catalogID",
        "catalogName",
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Other {
    @XmlElement(name = "catalogID")
    private String catalogID;

    @XmlElement(name = "catalogName")
    private String catalogName;
}
