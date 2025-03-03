package com.example.spring_redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SpringRedisApplicationTest {

    @Test
    void testMain() {
        assertDoesNotThrow(() -> SpringRedisApplication.main(new String[]{}));
    }
}
