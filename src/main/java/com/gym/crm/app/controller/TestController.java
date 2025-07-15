package com.gym.crm.app.controller;

import com.gym.dto.TraineeCreateRequest;
import com.gym.dto.TraineeRegistrationResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "Hello, Welcome to  Gym CRM!";
       }
}
