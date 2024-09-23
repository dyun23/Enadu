package org.example.backend.EmailVerify.Controller;

import lombok.RequiredArgsConstructor;

import org.example.backend.Common.BaseResponse;
import org.example.backend.EmailVerify.Service.EmailVerifyService;
import org.example.backend.Exception.custom.InvalidEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value="/email")
@RequiredArgsConstructor
public class EmailVerifyController {
    private final EmailVerifyService emailVerifyService;

    @PostMapping("/verify")
    public BaseResponse<String> verify(@RequestBody String email, @RequestBody String uuid){
            emailVerifyService.verifyEmail(email, uuid);
            return new BaseResponse<>("EMAIL VERIFICATION SUCCESS!");
    }

}
