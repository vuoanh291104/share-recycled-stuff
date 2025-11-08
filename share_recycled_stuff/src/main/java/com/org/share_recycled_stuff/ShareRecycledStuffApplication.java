package com.org.share_recycled_stuff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShareRecycledStuffApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShareRecycledStuffApplication.class, args);
    }

}
