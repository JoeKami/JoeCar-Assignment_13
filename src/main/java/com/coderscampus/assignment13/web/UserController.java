package com.coderscampus.assignment13.web;

import java.util.*;

import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/register")
	public String getCreateUser (ModelMap model) {
		
		model.put("user", new User());
		
		return "register";
	}
	
	@PostMapping("/register")
	public String postCreateUser (User user) {
		System.out.println(user);
		userService.saveUser(user);
		return "redirect:/register";
	}
	
	@GetMapping("/users")
	public String getAllUsers (ModelMap model) {
		Set<User> users = userService.findAll();
		
		model.put("users", users);
		if (users.size() == 1) {
			model.put("user", users.iterator().next());
		}
		
		return "users";
	}
	
	@GetMapping("/users/{userId}")
	public String getOneUser (ModelMap model, @PathVariable Long userId) {
		User user = userService.findById(userId);
		model.put("users", Arrays.asList(user));
		model.put("user", user);

		Address address = user.getAddress();
		if (address == null) {
			address = new Address();
		}
		model.put("address", address);

		List<Account> accounts = user.getAccounts();
		model.put("accounts", accounts);

		return "users";
	}
	
	@PostMapping("/users/{userId}")
	public String postOneUser (User user, @ModelAttribute Address address) {
		user.setAddress(address);
		if (user.getAddress() != null) {
			user.getAddress().setUser(user);
		}

		for (Account account : user.getAccounts()) {
			account.setUsers(Collections.singletonList(user));
		}
		userService.saveUser(user);
		return "redirect:/users/"+user.getUserId();
	}
	
	@PostMapping("/users/{userId}/delete")
	public String deleteOneUser (@PathVariable Long userId) {
		userService.delete(userId);
		return "redirect:/users";
	}

	@PostMapping("/users/{userId}/accounts")
	public String postNewAccount (@PathVariable Long userId, @ModelAttribute Account account) {
		if (account.getAccountId() != null) {
			account.setAccountId(null);
		}

		User user = userService.findById(userId);
		if (account.getAccountId() == null) {
			account.setUsers(Collections.singletonList(user));
			user.getAccounts().add(account);
		}

		Account newAccount = userService.saveAccount(account);

		return "redirect:/users/"+userId+"/accounts/"+newAccount.getAccountId();
	}

	@GetMapping("/users/{userId}/accounts/{accountId}")
	public String getOneAccount (ModelMap model, @PathVariable Long userId, @PathVariable Long accountId) {
		User user = userService.findById(userId);
		model.put("user", user);

		Account account = userService.findAccountById(accountId);
		model.put("account", account);

		return "account";
	}

	@PostMapping("/users/{userId}/accounts/{accountId}")
	public String postOneAccount (@PathVariable Long accountId, @PathVariable Long userId, @ModelAttribute Account account) {
		User user = userService.findById(userId);
		Account existingAccount = userService.findAccountById(accountId);
		existingAccount.setAccountName(account.getAccountName());

		userService.saveAccount(existingAccount);
		return "redirect:/users/"+userId;
	}
}
