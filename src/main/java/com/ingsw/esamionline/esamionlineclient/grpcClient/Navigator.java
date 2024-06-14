package com.ingsw.esamionline.esamionlineclient.grpcClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class Navigator implements Serializable {
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";
    private static final String LOGIN = "login"+"?faces-redirect=true";
    private static final String LOGINbutton = "login";
    private static final String DISPONIBILI = "home.xhtml"+"?faces-redirect=true";
    private static final String PRENOTATI = "prenotazioni"+"?faces-redirect=true";
    private static final String COMPLETATI = "completati"+"?faces-redirect=true";
    private static final String APPELLO = "appello";

    private boolean userLoggedIn = false;

    private String currentPage = LOGIN;

    public String goto_disponibili(){
        if (!userLoggedIn) return LOGIN;
        if (!currentPage.equals(DISPONIBILI)){
            currentPage=DISPONIBILI;
        }
        System.out.println("LOGOUTS");
        return DISPONIBILI;
    }

    public String goto_prenotati(){
        if (!userLoggedIn) return LOGIN;
        if (!currentPage.equals(PRENOTATI)){
            currentPage=PRENOTATI;
        }
        return PRENOTATI;
    }

    public String goto_completati(){
        if (!userLoggedIn) return LOGIN;
        if (!currentPage.equals(COMPLETATI)){
            currentPage=COMPLETATI;
        }
        return COMPLETATI;
    }

    public String goto_appello(){
        if (!userLoggedIn) return LOGIN;
        if (!currentPage.equals(APPELLO)){
            currentPage=APPELLO;
        }
        return APPELLO;
    }

    public String passlogin(boolean passed){
        if (!passed) return LOGIN;
        userLoggedIn=true;
        return DISPONIBILI;
    }
    public String logout(){
        userLoggedIn=false;
        return LOGIN;
    }
    public String checkLogin(){
        if (!userLoggedIn) return LOGIN;
        return SUCCESS;
    }
}
