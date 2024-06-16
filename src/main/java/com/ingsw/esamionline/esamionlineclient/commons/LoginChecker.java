package com.ingsw.esamionline.esamionlineclient.commons;

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
    public Object admincheckLogin() {
        return navigator.checkLoginAdmin();
    }

}
