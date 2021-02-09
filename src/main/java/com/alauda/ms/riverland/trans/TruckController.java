package com.alauda.ms.riverland.trans;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/truck")
public class TruckController {

    @GetMapping("/success")
    public String success() {
        return "Success";
    }

    @GetMapping("/failure")
    public String failure() {
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "This is a remote client exception");
//        return "failure";
    }
}
