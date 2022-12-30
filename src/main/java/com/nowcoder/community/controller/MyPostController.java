package com.nowcoder.community.controller;


import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MyPostService;
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
public class MyPostController {

    @Autowired
    private MyPostService myPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/posts/{userId}", method = RequestMethod.GET)
    @LoginRequired
    public String getMyPosts(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        model.addAttribute("user", user);

        int postCount = (int) myPostService.findPostCount(userId);

        model.addAttribute("postCount", postCount);

        page.setLimit(5);
        page.setPath("/posts/" + userId);
        page.setRows(postCount);

        List<Map<String, Object>> list = myPostService.findPosts(userId, page.getOffset(), page.getLimit(), 0);

        model.addAttribute("list", list);
        return "site/my-post";
    }
}

