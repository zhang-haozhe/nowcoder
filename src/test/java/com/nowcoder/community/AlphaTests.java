package com.nowcoder.community;


import com.nowcoder.community.service.MyCommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class AlphaTests {

    @Autowired
    private MyCommentService myCommentService;

    @Test
    public void test() {
        System.out.println(myCommentService.findCommentCount(153));
    }
}
