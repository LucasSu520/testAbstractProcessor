package com.example.proxy;

import com.example.annotations.CalTime;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HandlerProxy  {
    @Test
    public void test() {
        intercept();
    }


    @CalTime
    public void intercept(){
        for (int i = 0; i < 10; i++) {
            System.out.println("d大家好" + i);
        }
    }
}
