package example;

import lib.annotation.Controller;
import lib.annotation.web.GetMapping;
import lib.annotation.web.RequestParam;


@Controller
public class HelloController {
    @GetMapping("/hello")
    public String hello(
            @RequestParam("name") String name
    ) {
        return "Hello " + name;
    }
}
