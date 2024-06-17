package com.ingsw.esamionline.esamionlineclient;

import application.persistance.pojos.Appello;
import application.persistance.pojos.Risultato;
import application.persistance.pojos.Student;
import com.ingsw.esamionline.esamionlineclient.grpcClient.ClientMethods;
import gen.javaproto.CompletedAppello;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class StudenteSession implements Serializable {
    private Student student = new Student();
    private List<Appello> disponibili = new ArrayList<>();
    private String mat = "";
    private String cf = "";
    private long idAppello = -1;

    private ClientMethods reqDispatcher = new ClientMethods();
    private long deadline;
    private Appello appelloselezionato  = new Appello();

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public ClientMethods getReqDispatcher() {
        return reqDispatcher;
    }

    public void setReqDispatcher(ClientMethods reqDispatcher) {
        this.reqDispatcher = reqDispatcher;
    }


    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<Appello> getDisponibili() {
        return disponibili;
    }

    public void setDisponibili(List<Appello> disponibili) {
        this.disponibili = disponibili;
    }

    public String getMat() {
        return mat;
    }

    public void setMat(String mat) {
        this.mat = mat;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public long getIdAppello() {
        return idAppello;
    }

    public void setIdAppello(long idAppello) {
        this.idAppello = idAppello;
    }


    public Appello getAppelloselezionato() {
        return appelloselezionato;
    }

    public void setAppelloselezionato(Appello appelloselezionato) {
        this.appelloselezionato = appelloselezionato;
    }
}
