package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussionPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author haozhe
 */
@Mapper
public interface DiscussionPostMapper {

    List<DiscussionPost> selectDiscussionPosts(int userId, int offset, int limit, int orderMode);

    // @Param annotation is used to give param a name
    // if only one is given and used in tag <if>, then a name must be given.
    int selectDiscussionPostRows(@Param("userId") int userId);

    int insertDiscussionPost(DiscussionPost discussionPost);

    DiscussionPost selectDiscussionPostById(int id);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
