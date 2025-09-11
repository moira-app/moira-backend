package com.org.server.util;

import java.util.Random;

public class RandomCharSet {

    private static final String [] randomChar=new String[] {
            "A","B","C","D","E","F","G","H","1","2","3","4","5","6","7","8","9","0"
            ,"J","K","L","Z","X","C","V","N","M"
    };

    public static String createRandomName(){
        String name="";
        for(int i=0;i<10;i++){
            Random random = new Random();
            int number = random.nextInt(27); // 0 ~ 26 포함;
            name+=randomChar[number];
        }
        return name;
    }
}
