package com.nowcoder.community;


import com.nowcoder.community.entity.DiscussionPost;
import com.nowcoder.community.service.DiscussionPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTests {

    @Autowired
    private DiscussionPostService discussionPostService;

    private DiscussionPost data;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass");
    }

    @Before
    public void before() {
        System.out.println("before");
        // init test data
        data = new DiscussionPost();
        data.setUserId(111);
        data.setTitle("Test Title");
        data.setContent("Test Body");
        data.setCreateTime(new Date());
        discussionPostService.addDiscussionPost(data);
    }

    @After
    public void after() {
        System.out.println("after");
        // delete test data
        discussionPostService.updateStatus(data.getId(), 2);
    }

    @Test
    public void test1() {
        System.out.println("test1");
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    public void testFindById() {
        DiscussionPost post = discussionPostService.findDiscussionPostById(data.getId());
        Assert.assertNotNull(post);
        Assert.assertEquals(data.getTitle(), post.getTitle());
        Assert.assertEquals(data.getContent(), post.getContent());
    }

    @Test
    public void testUpdateScore() {
        int rows = discussionPostService.updateScore(data.getId(), 2000.00);
        Assert.assertEquals(1, rows);

        DiscussionPost post = discussionPostService.findDiscussionPostById(data.getId());
        Assert.assertEquals(2000.00, post.getScore(), 2);
    }

}
