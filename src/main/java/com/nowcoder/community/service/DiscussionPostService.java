package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.entity.DiscussionPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussionPostService {

    @Autowired
    private DiscussionPostMapper discussionPostMapper;

    public List<DiscussionPost> findDiscussionPosts(int userId, int offset, int limit) {
        return discussionPostMapper.selectDiscussionPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussionPostMapper.selectDiscussionPostRows(userId);
    }
}
