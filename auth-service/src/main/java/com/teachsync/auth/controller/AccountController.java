package com.teachsync.auth.controller;

import com.teachsync.auth.model.AccountInfoResponse;
import com.teachsync.interaction.feign.client.UserAuthClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teachsync/account")
public class AccountController {

    private final UserAuthClient userAuthClient;

    public AccountController(UserAuthClient userAuthClient) {
        this.userAuthClient = userAuthClient;
    }

    @GetMapping("/info")
    public ResponseEntity<AccountInfoResponse> getAccountInfo(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userAuthClient.getUserInfo(email));
    }
}
