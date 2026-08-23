package com.hen.flastcard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("/test.properties")
class FlastcardApplicationTests {

    @Test
    void contextLoads() {}
}
