package com.ingsw.esamionline.esamionlineclient.grpcClient;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class LoginChecker {
    @Inject
    private Navigator navigator;
    public Object checkLogin() {
        return navigator.checkLogin();
    }
}
