package com.imadcn.system.test.idworker;

import com.imadcn.framework.idworker.jackson.JSON;
import com.imadcn.framework.idworker.jackson.JacksonConfig;
import com.imadcn.system.test.spring.User;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author Anson <br>
 * Created on 2020/11/20 17:12<br>
 * 名称：JSONTest.java <br>
 * 描述：JSON测试<br>
 */
public class JSONTest {

    @Test
    public void testJSON() {
        JSON json=new JSON(new JacksonConfig().objectMapper());
        //toStr
        User user=new User();
        user.setName("json");
        user.setDateTime(LocalDateTime.now());
        user.setDate(new Date());
        user.setLongValue(Long.valueOf(12));
        String str=json.objToStr(user);
        System.out.println("toStr: "+str);
        //toObj
        System.out.println("toObj: "+json.strToObj(str, User.class));
        System.out.println("转两次结果："+json.objToStr(json.strToObj(str, User.class)));
    }
}
