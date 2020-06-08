package org.jeecg.modules.online.cgform.a;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AextTest {

    @Autowired
    private Aext aext;


    @Test
    public void cgformTable() {
        aext.runB();
    }
}