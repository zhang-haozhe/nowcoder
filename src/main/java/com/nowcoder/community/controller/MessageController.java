package com.nowcoder.community.controller;


import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;


    // list of messages
    @RequestMapping(path = "/messages", method = RequestMethod.GET)
    public String getMessages(Model model, Page page) {
        User user = hostHolder.getUser();
        // pagination info
        page.setLimit(5);
        page.setPath("/messages");
        page.setRows(messageService.findConversationCount(user.getId()));
        // conversations
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit()
        );

        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("messageCount", messageService.findMessageCount(message.getConversationId()));
                map.put("unreadCount", messageService.findUnreadMessageCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // query the number of unread messages
        int unreadMessageCount = messageService.findUnreadMessageCount(user.getId(), null);
        model.addAttribute("unreadMessageCount", unreadMessageCount);
        int unreadNotificationCount = messageService.findUnreadNotificationCount(user.getId(), null);
        model.addAttribute("unreadNotificationCount", unreadNotificationCount);

        return "/site/message";
    }

    @RequestMapping(path = "/message/{conversationId}", method = RequestMethod.GET)
    public String getMessageDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // pagination info
        page.setLimit(5);
        page.setPath("/message/" + conversationId);
        page.setRows(messageService.findMessageCount(conversationId));

        // list of messages
        List<Message> messageList = messageService.findMessages(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> messages = new ArrayList<>();
        if (messageList != null) {
            for (Message message : messageList) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                messages.add(map);
            }
        }

        model.addAttribute("messages", messages);

        // message target
        model.addAttribute("target", getMessageTarget(conversationId));

        // set as read
        List<Integer> ids = getMessageIds(messageList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/message-detail";
    }

    private List<Integer> getMessageIds(List<Message> messageList) {
        List<Integer> ids = new ArrayList<>();

        if (messageList != null) {
            for (Message message : messageList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    private User getMessageTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);


        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        }
        return userService.findUserById(id0);
    }

    @RequestMapping(path = "/message/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "Target user does not exist");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/message/delete", method = RequestMethod.POST)
    @LoginRequired
    public String deleteMessage(int id, String conversationId) {
        messageService.deleteMessage(id);
        if (conversationId.equals(TOPIC_COMMENT) || conversationId.equals(TOPIC_FOLLOW) || conversationId.equals(TOPIC_LIKE)) {
            return "redirect:/notification/" + conversationId;
        }
        return "redirect:/message/" + conversationId;
    }


    @RequestMapping(path = "/notifications", method = RequestMethod.GET)
    public String getNotifications(Model model) {
        User user = hostHolder.getUser();

        // comment
        Message message = messageService.findLatestNotification(user.getId(), TOPIC_COMMENT);
        if (message != null) {
            Map<String, Object> commentMessageVO = new HashMap<>();
            commentMessageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            commentMessageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            commentMessageVO.put("entityType", data.get("entityType"));
            commentMessageVO.put("entityId", data.get("entityId"));
            commentMessageVO.put("postId", data.get("postId"));

            int count = messageService.findNotificationCount(user.getId(), TOPIC_COMMENT);
            commentMessageVO.put("count", count);

            int unread = messageService.findUnreadNotificationCount(user.getId(), TOPIC_COMMENT);
            commentMessageVO.put("unread", unread);
            model.addAttribute("commentNotification", commentMessageVO);
        }

        // like
        message = messageService.findLatestNotification(user.getId(), TOPIC_LIKE);
        if (message != null) {
            Map<String, Object> likeMessageVO = new HashMap<>();
            likeMessageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            likeMessageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            likeMessageVO.put("entityType", data.get("entityType"));
            likeMessageVO.put("entityId", data.get("entityId"));
            likeMessageVO.put("postId", data.get("postId"));

            int count = messageService.findNotificationCount(user.getId(), TOPIC_LIKE);
            likeMessageVO.put("count", count);

            int unread = messageService.findUnreadNotificationCount(user.getId(), TOPIC_LIKE);
            likeMessageVO.put("unread", unread);
            model.addAttribute("likeNotification", likeMessageVO);
        }

        // follow
        message = messageService.findLatestNotification(user.getId(), TOPIC_FOLLOW);
        if (message != null) {
            Map<String, Object> followMessageVO = new HashMap<>();
            followMessageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            followMessageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            followMessageVO.put("entityType", data.get("entityType"));
            followMessageVO.put("entityId", data.get("entityId"));

            int count = messageService.findNotificationCount(user.getId(), TOPIC_FOLLOW);
            followMessageVO.put("count", count);

            int unread = messageService.findUnreadNotificationCount(user.getId(), TOPIC_FOLLOW);
            followMessageVO.put("unread", unread);
            model.addAttribute("followNotification", followMessageVO);
        }

        // query the count of unread notifications
        int unreadMessageCount = messageService.findUnreadMessageCount(user.getId(), null);
        model.addAttribute("unreadMessageCount", unreadMessageCount);
        int unreadNotificationCount = messageService.findUnreadNotificationCount(user.getId(), null);
        model.addAttribute("unreadNotificationCount", unreadNotificationCount);

        return "site/notice";
    }

    @RequestMapping(path = "/notification/{topic}", method = RequestMethod.GET)
    public String getNotification(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notification/" + topic);
        page.setRows(messageService.findNotificationCount(user.getId(), topic));

        List<Message> notifications = messageService.findNotifications(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> notificationVOs = new ArrayList<>();
        if (notifications != null) {
            for (Message notification : notifications) {
                Map<String, Object> map = new HashMap<>();
                // notifications
                map.put("notification", notification);
                //content
                String content = HtmlUtils.htmlUnescape(notification.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                map.put("conversationId", data.get("conversationId"));
                // notification auther
                map.put("fromUser", userService.findUserById(notification.getFromId()));

                notificationVOs.add(map);
            }
        }
        model.addAttribute("notifications", notificationVOs);

        // mark as read
        List<Integer> ids = getMessageIds(notifications);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }


}
