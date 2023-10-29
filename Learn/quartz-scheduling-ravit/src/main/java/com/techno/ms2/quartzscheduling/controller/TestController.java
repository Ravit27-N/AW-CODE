package com.techno.ms2.quartzscheduling.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping
    public String helloRavit() {
        return "Hello RAVIT FROM APP2";
    }
}
