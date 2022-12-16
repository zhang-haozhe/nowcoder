package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // query current user's conversations, where only the newest message is returned.
    List<Message> selectConversations(int userId, int offset, int limit);

    // query the number of current user's conversations.
    int selectConversationCount(int userId);

    // query messages of a single conversation
    List<Message> selectMessages(String conversationId, int offset, int limit);

    // query the count of messages of a conversation
    int selectMessageCount(String conversationId);

    // query the number of unread messages
    int selectUnreadMessageCount(int userId, String conversationId);

    // adding new message
    int insertMessage(Message message);

    // modifying message status
    int updateStatus(List<Integer> ids, int status);

    int deleteMessage(int id);
}
