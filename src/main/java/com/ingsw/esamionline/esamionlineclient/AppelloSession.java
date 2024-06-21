package com.ingsw.esamionline.esamionlineclient;

import application.persistance.pojos.Appello;
import application.persistance.pojos.Domanda;
import application.persistance.pojos.Risposta;
import com.ingsw.esamionline.esamionlineclient.commons.Navigator;
import com.ingsw.esamionline.esamionlineclient.grpcClient.ClientMethods;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Named
@SessionScoped
public class AppelloSession implements Serializable {

    @Inject
    private StudenteSession student;
    @Inject
    private ClientMethods dispatcher;
    @Inject
    private Navigator navigator;
    private List<Domanda> domande;


    public List<String> getRispostedate() {
        return rispostedate;
    }

    public void setRispostedate(List<String> rispostedate) {
        this.rispostedate = rispostedate;
    }

    private List<String> rispostedate;
    private Domanda curDomanda;
    private int indexcur=0;
    private int secAppello = 600;
    private int secDomanda = 5;
    private int tDomandaIniziale = 5;

    public String getSelezione() {
        return selezione;
    }

    public void setSelezione(String selezione) {
        this.selezione = selezione;
    }

    private String selezione = null;
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

    public void  inizializzaEsame(){
        indexcur = 0;
        Appello cur = student.getAppelloselezionato();
        domande = cur.getDomande();
        ultimaDomanda = indexcur == domande.size()-1;
        rispostedate = new LinkedList<>();
        curDomanda = domande.get(indexcur);
        secAppello = (int) TimeUnit.MINUTES.toSeconds(cur.getDurata_minuti());
        secDomanda = cur.getTempo_domanda_sec();
        tDomandaIniziale = cur.getTempo_domanda_sec();
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
        if(ultimaDomanda)
            dispatcher.terminaAppello();
        else{
            indexcur++;
            secDomanda = tDomandaIniziale;
            rispostedate.add(selezione);
            selezione=null;
            curDomanda = domande.get(indexcur);
            secDomanda = student.getAppelloselezionato().getTempo_domanda_sec();
            if (indexcur == domande.size()-1)
                ultimaDomanda=true;
            PrimeFaces.current().ajax().update("header", "response");
        }

    }


    /**
     * calcola il punteggio ottenuto, tenendo conto dell'utlima risposta data come ancora non inserita
     */
    public void calcolapunteggio() {
        int sum = 0;
        rispostedate.add(selezione);
        System.out.println(rispostedate);
        for (int i = 0 ; i<domande.size(); i++){
            String rispData = rispostedate.get(i);
            Domanda domanda = domande.get(i);
            if (rispData == null) {
                sum--;
                continue;
            }
            for (Risposta r : domanda.getRisposte()){
                if (r.getText().equals(rispData) && r.isCorretta()){
                        sum+=3;
                        break;
                }
            }
        }
        float part = (float) sum/(3*domande.size());
        if (part<0) part=0;
        punteggio= Math.round(30*(part));
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

    public void log(){
        if (selezione == null)
            System.out.println("change> null");
        else
            System.out.println("change>"+selezione);
    }
}
