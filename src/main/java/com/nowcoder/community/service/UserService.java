package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;


    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // handling empty values
        if (user == null) {
            throw new IllegalArgumentException("Argument can't be empty");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMessage", "User name can't be empty");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMessage", "Password can't be empty");
            return map;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMessage", "Email can't be empty");
            return map;
        }

        // validating account
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMessage", "Account already exists");
            return map;
        }

        // validating email
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMessage", "Email already exists");
            return map;
        }

        // registering user
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // activation email
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://nowcoder.com/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "Verify your account", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return REPEATED_ACTIVATION;
        } else if (user.getActivationCode().equals((code))) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }

    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // null value handling
        if (StringUtils.isBlank(username)) {
            map.put("usernameMessage", "Username can't be empty!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMessage", "Password can't be empty!");
            return map;
        }

        // validating account
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMessage", "Username does not exist!");
            return map;
        }

        // validating status
        if (user.getStatus() == 0) {
            map.put("usernameMessage", "Account not activated!");
            return map;
        }

        //validating password
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMessage", "Password incorrect!");
            return map;
        }

        // generating login credentials
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertloginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }
}
