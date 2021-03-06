package ru.geekbrains.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.geekbrains.persistance.User;
import ru.geekbrains.persistance.UserRepository;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String allUsers(Model model) throws SQLException {
        List<User> allUsers = userRepository.getAllUsers();
        model.addAttribute("users", allUsers);
        return "users";
    }

    @GetMapping("/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) throws SQLException {
        User user = userRepository.findById(id);
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping("/add")
    public String addUser(User user,Model model) throws SQLException{
        List<User> allUsers = userRepository.getAllUsers();
        allUsers.add(user);
        userRepository.insert(user);
        return "redirect:/user";
    }

    @PostMapping("/update")
    public String updateUser(User user) throws SQLException {
        userRepository.update(user);
        return "redirect:/user";
    }
}
