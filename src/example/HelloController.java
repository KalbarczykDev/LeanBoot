package example;

import lib.annotation.Controller;
import lib.annotation.web.GetMapping;


@Controller
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello from LeanBoot!";
    }
}
