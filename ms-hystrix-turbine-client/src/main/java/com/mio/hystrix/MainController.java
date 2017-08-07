package com.mio.hystrix;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MainController {


    @RequestMapping("index")
    public String hello() {
        return "index";

    }


}
