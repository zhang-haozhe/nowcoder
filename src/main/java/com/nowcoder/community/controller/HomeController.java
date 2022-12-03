package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussionPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussionPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussionPostService discussionPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        // before accessing the function, Spring MVC automatically instantiates Model and Page, and injects Page into Model.
        // therefore, we can directly access data in the Page object within thymeleaf
        page.setRows(discussionPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussionPost> list = discussionPostService.findDiscussionPosts(0, page.getOffset(), page.getLimit());

        List<Map<String, Object>> discussionPosts = new ArrayList<>();

        if (list != null) {
            for (DiscussionPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussionPosts.add(map);
            }
        }

        model.addAttribute("discussionPosts", discussionPosts);
        return "/index";
    }
}
