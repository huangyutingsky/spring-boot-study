package org.example.entity;

import lombok.Data;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/24 19:22
 */
@Data
public class Bean {
    private String name;

    private ABC abc;

    @Data
    public class ABC{
        private String age;

        public void test(){

        }
    }
}

@Data
class ABC{
    private String age;
}
