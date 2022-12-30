package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussionPost;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MyPostService implements CommunityConstant {

    @Autowired
    private DiscussionPostService discussionPostService;

    @Autowired
    private LikeService likeService;

    public long findPostCount(int userId) {
        return discussionPostService.findDiscussPostRows(userId);
    }

    public List<Map<String, Object>> findPosts(int userId, int offset, int limit, int orderMode) {
        List<DiscussionPost> posts = discussionPostService.findDiscussionPosts(userId, offset, limit, orderMode);
        if (posts == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (DiscussionPost post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            long postLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            map.put("likeCount", postLikeCount);
            list.add(map);
        }

        return list;
    }
}
