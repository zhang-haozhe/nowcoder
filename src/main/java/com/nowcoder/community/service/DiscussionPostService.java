package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.controller.DiscussionPostController;
import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.entity.DiscussionPost;
import com.nowcoder.community.util.SensitiveWordFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussionPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussionPostController.class);
    @Autowired
    private DiscussionPostMapper discussionPostMapper;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;


    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.exipre-seconds}")
    private int expireSeconds;

    private LoadingCache<String, List<DiscussionPost>> postListCache;

    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussionPost>>() {
                    @Override
                    public @Nullable List<DiscussionPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("Argument error");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("Argument error");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);


                        return discussionPostMapper.selectDiscussionPosts(0, offset, limit, 1);
                    }
                });
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussionPostMapper.selectDiscussionPostRows(key);
                    }
                });
    }

    public List<DiscussionPost> findDiscussionPosts(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }

        logger.debug("load post list from DB.");
        return discussionPostMapper.selectDiscussionPosts(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }

        logger.debug("load post rows from DB.");
        return discussionPostMapper.selectDiscussionPostRows(userId);
    }

    public int addDiscussionPost(DiscussionPost post) {
        if (post == null) {
            throw new IllegalArgumentException("Argument can't be empty");
        }

        //transcribing HTML tags
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //filtering sensitive words
        post.setTitle(sensitiveWordFilter.filter(post.getTitle()));
        post.setContent(sensitiveWordFilter.filter(post.getContent()));

        return discussionPostMapper.insertDiscussionPost(post);
    }

    public DiscussionPost findDiscussionPostById(int id) {
        return discussionPostMapper.selectDiscussionPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussionPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussionPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussionPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussionPostMapper.updateScore(id, score);
    }
}
