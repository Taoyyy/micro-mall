package com.yuan.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class SecurityApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testBCryptPasswordEncoder(){
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        final String encode = passwordEncoder.encode("666");
        final String encode2 = passwordEncoder.encode("666");
        final boolean matches = passwordEncoder.matches("666", "$2a$10$lmK28YgvUaISEX6LDf2haOh8vcPMa.CYbhEW5qpAn8KXGSuffc6eK");
        System.out.println(encode);
        System.out.println(encode2);
        System.out.println(matches);

    }

    @Test
    public void testJwt(){


    }

}
