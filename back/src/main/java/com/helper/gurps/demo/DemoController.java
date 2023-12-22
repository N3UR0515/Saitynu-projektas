package com.helper.gurps.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/demo-controller")
public class DemoController {
    @GetMapping
    public ResponseEntity<String> sayHello()
    {
        System.out.println("can anyone hear me?");
        return ResponseEntity.ok("Hello.");
    }
}
