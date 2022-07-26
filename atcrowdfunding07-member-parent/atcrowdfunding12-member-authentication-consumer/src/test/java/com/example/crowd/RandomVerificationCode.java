package com.example.crowd;

import org.junit.Test;

import java.util.Random;

public class RandomVerificationCode {

    @Test
    public void test01() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 4; i++){
            int index = random.nextInt(10);
            code += index;
        }
        System.out.println(code);
    }
}
