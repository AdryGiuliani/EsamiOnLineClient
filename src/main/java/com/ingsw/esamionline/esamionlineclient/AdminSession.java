package com.ingsw.esamionline.esamionlineclient;

import application.persistance.pojos.Appello;
import application.persistance.pojos.Options;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Named
@SessionScoped
public class AdminSession implements Serializable {

    private boolean loggedIn = false;
    private String username = "";
    private String password = "";
    private List<Appello> allAppellos = new ArrayList<>();
    private Appello selectedAppello;
    private Options opzioni;
    private boolean modificaAppello = false;
    private long deadline_h;

    public long getDeadline_h() {
        return deadline_h;
    }

    public void setDeadline_h(long deadline_h) {
        this.deadline_h = deadline_h;
    }

    public boolean isModificaAppello() {
        return modificaAppello;
    }

    public void setModificaAppello(boolean modificaAppello) {
        this.modificaAppello = modificaAppello;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public List<Appello> getAllAppellos() {
        return allAppellos;
    }

    public void setAllAppellos(List<Appello> allAppellos) {
        this.allAppellos = allAppellos;
    }

    public Appello getSelectedAppello() {
        return selectedAppello;
    }

    public void setSelectedAppello(Appello selectedAppello) {
        this.selectedAppello = selectedAppello;
    }

    public Options getOpzioni() {
        return opzioni;
    }

    public void setOpzioni(Options opzioni) {
        this.opzioni = opzioni;
    }
}
