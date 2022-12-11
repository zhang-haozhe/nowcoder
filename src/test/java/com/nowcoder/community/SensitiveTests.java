package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveWordFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Test
    public void testSensitiveWrodFilter() {
        String text = "可以赌博，可以嫖👴娼，可以吸⭐毒，可以开 票，lol";
        text = sensitiveWordFilter.filter(text);
        System.out.println(text);
    }
}
