package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findMessages(String conversationId, int offset, int limit) {
        return messageMapper.selectMessages(conversationId, offset, limit);
    }

    public int findMessageCount(String conversationId) {
        return messageMapper.selectMessageCount(conversationId);
    }

    public int findUnreadMessageCount(int userId, String conversationId) {
        return messageMapper.selectUnreadMessageCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveWordFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    public int deleteMessage(int id) {
        return messageMapper.deleteMessage(id);
    }

    public Message findLatestNotification(int userId, String topic) {
        return messageMapper.selectLatestNotification(userId, topic);
    }

    public int findNotificationCount(int userId, String topic) {
        return messageMapper.selectNotificationCount(userId, topic);
    }

    public int findUnreadNotificationCount(int userId, String topic) {
        return messageMapper.selectUnreadNotificationCount(userId, topic);
    }

    public List<Message> findNotifications(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotifications(userId, topic, offset, limit);
    }
}
