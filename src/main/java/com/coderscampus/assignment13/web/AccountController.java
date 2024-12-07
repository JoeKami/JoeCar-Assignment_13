package com.coderscampus.assignment13.web;

import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/{userId}/accounts")
    public String postNewAccount(@PathVariable Long userId, @ModelAttribute Account account) {
//        if (account.getAccountId() != null) {
//            account.setAccountId(null);
//        }

        User user = userService.findById(userId);
        if (account.getAccountId() == null) {
            account.setUsers(Collections.singletonList(user));
            user.getAccounts().add(account);
        }

        Account newAccount = userService.saveAccount(account);

        return "redirect:/users/" + userId + "/accounts/" + newAccount.getAccountId();
    }

    @GetMapping("/users/{userId}/accounts/{accountId}")
    public String getOneAccount(ModelMap model, @PathVariable Long userId, @PathVariable Long accountId) {
        User user = userService.findById(userId);
        model.put("user", user);

        Account account = userService.findAccountById(accountId);
        model.put("account", account);

        return "account";
    }

    @PostMapping("/users/{userId}/accounts/{accountId}")
    public String postOneAccount(@PathVariable Long accountId, @PathVariable Long userId, @ModelAttribute Account account) {
        User user = userService.findById(userId);
        Account existingAccount = userService.findAccountById(accountId);
        existingAccount.setAccountName(account.getAccountName());

        userService.saveAccount(existingAccount);
        return "redirect:/users/" + userId;
    }
}