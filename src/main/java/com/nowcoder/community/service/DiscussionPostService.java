package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.entity.DiscussionPost;
import com.nowcoder.community.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussionPostService {

    @Autowired
    private DiscussionPostMapper discussionPostMapper;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    public List<DiscussionPost> findDiscussionPosts(int userId, int offset, int limit) {
        return discussionPostMapper.selectDiscussionPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
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
}
