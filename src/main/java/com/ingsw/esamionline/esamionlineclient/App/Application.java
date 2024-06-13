package com.ingsw.esamionline.esamionlineclient.App;

import application.services.ServerEsamiOnLine;
import com.ingsw.esamionline.esamionlineclient.StudenteSession;

import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                try {
                    ServerEsamiOnLine.start();
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
//        StudenteSession s = new StudenteSession();
//        s.setMat("111");
//        s.setCf("AAA");
//        s.login();
    }
}
