package com.kiheyunkim.simplechatting.com.kiheyunkim.simplechatting;

public class Util {
    static public String randomName() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 6; ++i) {
            double random = Math.random();
            if (((int) (random * 100)) % 2 == 1) {
                stringBuilder.append((char) ((int) 'a' + (int) (Math.random() * 26)));
            } else {
                stringBuilder.append((char) ((int) '0' + (int) (Math.random() * 10)));
            }
        }

        return stringBuilder.toString();
    }
}
