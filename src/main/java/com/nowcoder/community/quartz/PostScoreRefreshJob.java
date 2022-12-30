package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.DiscussionPost;
import com.nowcoder.community.service.DiscussionPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunityConstant {


    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    // init epoch
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("MM-dd-yyyy").parse("08-01-2014 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("Failed to initialize the init epoch", e);
        }
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussionPostService discussionPostService;
    @Autowired
    private LikeService likeService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("task cancelled: no post to refresh");
            return;
        }

        logger.info("refreshing post scores... " + operations.size());

        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }

        logger.info("finished refreshing scores");
    }

    private void refresh(int postId) {
        DiscussionPost post = discussionPostService.findDiscussionPostById(postId);

        if (post == null) {
            logger.error("post does not exist: id = " + postId);
            return;
        }

        boolean isFeature = post.getStatus() == 1;

        int commentCount = post.getCommentCount();

        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        double w = (isFeature ? 75 : 0) + commentCount * 10 + likeCount * 2;

        double score = Math.log10(Math.max(w, 1)) + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        discussionPostService.updateScore(postId, score);

    }
}
