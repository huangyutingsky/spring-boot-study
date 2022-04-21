package org.example.entity;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/23 14:43
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="Person")
@Scope("prototype")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "xmnl")
    private String xmnl;

    @XmlElement(name = "name")
    private String	name;

    @Autowired
    private MyProperty myProperty;
}
