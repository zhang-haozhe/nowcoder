package com.nowcoder.community.controller;


import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MyCommentService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class MyCommentController {
    @Autowired
    private UserService userService;

    @Autowired
    private MyCommentService myCommentService;

    @RequestMapping(path = "/comments/{userId}", method = RequestMethod.GET)
    @LoginRequired
    public String getMyComments(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        model.addAttribute("user", user);

        int commentCount = (int) myCommentService.findCommentCount(userId);

        model.addAttribute("commentCount", commentCount);

        page.setLimit(5);
        page.setPath("/comments/" + userId);
        page.setRows(commentCount);

        List<Map<String, Object>> list = myCommentService.findComments(userId, page.getOffset(), page.getLimit());

        model.addAttribute("list", list);
        return "site/my-reply";
    }
}
