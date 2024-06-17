package com.ingsw.esamionline.esamionlineclient;

import application.persistance.pojos.Appello;
import application.persistance.pojos.Domanda;
import application.persistance.pojos.Risposta;
import com.ingsw.esamionline.esamionlineclient.commons.Navigator;
import jakarta.el.MethodExpression;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Named
@SessionScoped
public class AppelloSession implements Serializable {

    @Inject
    private StudenteSession student;
    @Inject
    private Navigator navigator;
    private List<Domanda> domande;
    private List<Risposta> rispostedate;
    private Domanda curDomanda;
    private int indexcur=0;
    private int secAppello = 600;
    private int secDomanda = 60;
    private Risposta selezione = null;
    private boolean ultimaDomanda;
    private int punteggio = 0;

    public int getSecDomanda() {
        return secDomanda;
    }

    public void setSecDomanda(int secDomanda) {
        this.secDomanda = secDomanda;
    }

    public int getSecAppello() {
        return secAppello;
    }

    public void setSecAppello(int secAppello) {
        this.secAppello = secAppello;
    }

    public StudenteSession getStudent() {
        return student;
    }

    public void setStudent(StudenteSession student) {
        this.student = student;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public List<Domanda> getDomande() {
        return domande;
    }

    public void setDomande(List<Domanda> domande) {
        this.domande = domande;
    }

    public List<Risposta> getRispostedate() {
        return rispostedate;
    }

    public void setRispostedate(List<Risposta> rispostedate) {
        this.rispostedate = rispostedate;
    }

    public Domanda getCurDomanda() {
        return curDomanda;
    }

    public void setCurDomanda(Domanda curDomanda) {
        this.curDomanda = curDomanda;
    }

    public int getIndexcur() {
        return indexcur;
    }

    public void setIndexcur(int indexcur) {
        this.indexcur = indexcur;
    }

    public String  inizializzaEsame(){
        Appello cur = student.getAppelloselezionato();
        rispostedate = new LinkedList<>();
        domande = cur.getDomande();
        curDomanda = domande.get(indexcur);
        secAppello = (int) TimeUnit.MINUTES.toSeconds(cur.getDurata_minuti());
        secDomanda = cur.getTempo_domanda_sec();
        return navigator.goto_esame();
    }


    public int getDurataSec(){
        return (int) TimeUnit.MINUTES.toSeconds(student.getAppelloselezionato().getDurata_minuti());
    }

    public void updatetimerA(){
        secAppello = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("time_app"));
    }

    public void updatetimerD(){
        secDomanda = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("time_dom"));
    }

    public void nextDomanda() {
        indexcur++;
        rispostedate.add(selezione);
        curDomanda = domande.get(indexcur);
        secDomanda = student.getAppelloselezionato().getTempo_domanda_sec();
        if (indexcur == domande.size()-1)
            ultimaDomanda=true;
    }

    public void onTimeoutAppello(){
        punteggio = calcolapunteggio();

    }

    private int calcolapunteggio() {
        int sum = 0;
        for (Risposta r : rispostedate){
            if (r == null)
                sum--;
            else if (r.isCorretta())
                sum+=3;
        }
        return Math.round(30*((float) sum /(3*domande.size())));
    }

    public Risposta getSelezione() {
        return selezione;
    }

    public void setSelezione(Risposta selezione) {
        this.selezione = selezione;
    }

    public boolean isUltimaDomanda() {
        return ultimaDomanda;
    }

    public void setUltimaDomanda(boolean ultimaDomanda) {
        this.ultimaDomanda = ultimaDomanda;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }
}
