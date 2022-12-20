package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussionPost;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussionPostService discussionPostService;

    public long findCommentCount(int userId) {
        return commentMapper.selectCommentCountByUser(userId);
    }

    public List<Map<String, Object>> findComments(int userId, int offset, int limit) {
        List<Comment> comments = commentMapper.selectCommentsByUser(userId, offset, limit);
        if (comments == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            DiscussionPost post = discussionPostService.findDiscussionPostById(comment.getEntityId());
            map.put("postTitle", post.getTitle());
            map.put("comment", comment);
            list.add(map);
        }

        return list;
    }
}
