package ar.edu.unq.epersgeist.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class HelloController {

    @PostMapping("/hello")
    public String hello(){
        return "Hello KEPERSONAJES!";
    }
}
