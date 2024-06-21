package com.ingsw.esamionline.esamionlineclient.commons;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.IOException;
import java.io.Serializable;

@Named
@SessionScoped
public class Navigator implements Serializable {
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";
    private static final String LOGIN = "login"+"?faces-redirect=true";
    private static final String LOGIN_ADMIN = "admin"+"?faces-redirect=true";
    private static final String HOME_ADMIN = "homeadmin"+"?faces-redirect=true";

    private static final String DISPONIBILI = "home"+"?faces-redirect=true";
    private static final String PRENOTATI = "prenotazioni"+"?faces-redirect=true";
    private static final String COMPLETATI = "completati"+"?faces-redirect=true";
    private static final String ESAME = "esame"+"?faces-redirect=true";

    private boolean userLoggedIn = false;
    private boolean adminLoggedIn = false;

    private String currentPage = LOGIN;

    public String goto_disponibili(){
        if (!userLoggedIn) return LOGIN;
        if (!currentPage.equals(DISPONIBILI)){
            currentPage=DISPONIBILI;
            return DISPONIBILI;
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    public String goto_prenotati(){
        if (!userLoggedIn) return LOGIN;
        if (!currentPage.equals(PRENOTATI)){
            currentPage=PRENOTATI;
            return PRENOTATI;
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    public String goto_completati(){
        if (!userLoggedIn) return LOGIN;
        if (!currentPage.equals(COMPLETATI)){
            currentPage=COMPLETATI;
            return COMPLETATI;
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    public String goto_esame( boolean pass){
        if (!userLoggedIn) return LOGIN;
        if (!currentPage.equals(ESAME) && pass){
            currentPage=ESAME;
            return ESAME;
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    public String passlogin(boolean passed){
        if (!passed) return FacesContext.getCurrentInstance().getViewRoot().getViewId();
        userLoggedIn=true;
        currentPage=DISPONIBILI;
        return DISPONIBILI;
    }
    public String passloginAdmin(boolean passed) {
        if (!passed) return FacesContext.getCurrentInstance().getViewRoot().getViewId();
        adminLoggedIn=true;
        currentPage=HOME_ADMIN;
        return HOME_ADMIN;
    }
    public String logout(){
        userLoggedIn=false;
        return LOGIN;
    }
    public String logoutAdmin(){
        adminLoggedIn=false;
        return LOGIN_ADMIN;
    }
    public String checkLogin(){
        if (!userLoggedIn) return LOGIN;
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }
    public String checkLoginAdmin(){
        if (!adminLoggedIn) return LOGIN_ADMIN;
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }



    private String curView(){
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    private String toAction(String id){
        return id + "?faces-redirect=true";
    }


    public void goto_prenotati_static() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("/esamionline-client-1.0/prenotazioni.xhtml");
    }
}
