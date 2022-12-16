package com.nowcoder.community.controller;


import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

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
        return "redirect:/message/" + conversationId;
    }

}
