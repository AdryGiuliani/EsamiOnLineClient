package com.ingsw.esamionline.esamionlineclient.grpcClient;

import application.App;
import application.persistance.pojos.Appello;
import application.persistance.pojos.Domanda;
import application.persistance.pojos.Options;
import application.persistance.pojos.Risposta;
import application.services.CapsuleDtoAssembler;
import application.validation.chainsteps.CapsuleValidate;
import com.ingsw.esamionline.esamionlineclient.AdminSession;
import com.ingsw.esamionline.esamionlineclient.commons.AdminRequester;
import com.ingsw.esamionline.esamionlineclient.commons.AuthCredentials;
import com.ingsw.esamionline.esamionlineclient.commons.Navigator;
import gen.javaproto.AppelloID;
import gen.javaproto.Credentials;
import gen.javaproto.Dto;
import gen.javaproto.Vuoto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import org.checkerframework.checker.units.qual.A;
import org.primefaces.PrimeFaces;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.model.DialogFrameworkOptions;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Named
@SessionScoped
public class AdminMethods implements Serializable {

    @Inject
    private AdminSession session;
    @Inject
    private Navigator navigator;

    @Inject
    private AdminRequester requester;

    private AuthCredentials credentials;

    private CapsuleDtoAssembler assembler = new CapsuleDtoAssembler();

    public String login(){
        if (!checkInserimento()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Validation", "Both fields are required!");
            PrimeFaces.current().dialog().showMessageDynamic(message);
            return navigator.passloginAdmin(false);
        }
        Credentials cred = Credentials.newBuilder().setMat(session.getUsername()).setCf(session.getPassword()).build();
        credentials = new AuthCredentials(cred);
        Dto dto = requester.getBlockingStub().withCallCredentials(credentials).login(cred);
        CapsuleValidate cap = assembler.disassembleValidate(dto);
        if (cap.getStatus()>=0){
            session.setAllAppellos(cap.getall_appelli());
            return navigator.passloginAdmin(true);
        }
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenziali non valide", cap.getException().getMessage());
        PrimeFaces.current().dialog().showMessageDynamic(message);
        return navigator.passloginAdmin(false);
    }

    public String logout(){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Logout in corso", "Arrivederci");
        PrimeFaces.current().dialog().showMessageDynamic(message);
        String out = navigator.logoutAdmin();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.invalidate();
        return out;
    }

    public void info(Appello app){
        session.setSelectedAppello(app);
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .fitViewport(true)
                .responsive(true)
                .width("900px")
                .contentWidth("100%")
                .resizeObserver(true)
                .resizeObserverCenter(true)
                .resizable(false)
                .styleClass("max-w-screen")
                .iframeStyleClass("max-w-screen")
                .build();

        PrimeFaces.current().dialog().openDynamic("/resources/appellodetails", options, null);
    }

    public void risultati(Appello app){
        session.setSelectedAppello(app);
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .fitViewport(true)
                .responsive(true)
                .width("900px")
                .contentWidth("100%")
                .resizeObserver(true)
                .resizeObserverCenter(true)
                .resizable(false)
                .styleClass("max-w-screen")
                .iframeStyleClass("max-w-screen")
                .build();

        PrimeFaces.current().dialog().openDynamic("/resources/appellorisultati", options, null);
    }

    public void addAppello(){
        session.setModificaAppello(false);
        session.setSelectedAppello(new Appello());
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .fitViewport(true)
                .responsive(true)
                .width("900px")
                .contentWidth("100%")
                .resizeObserver(true)
                .resizeObserverCenter(true)
                .resizable(false)
                .styleClass("max-w-screen")
                .iframeStyleClass("max-w-screen")
                .build();
        PrimeFaces.current().dialog().openDynamic("/resources/appelloeditor", options, null);
    }
    public void modify(Appello app){
        session.setModificaAppello(true);
        session.setSelectedAppello(app); //MAY USE PROTOTYPE
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .fitViewport(true)
                .responsive(true)
                .width("900px")
                .contentWidth("100%")
                .resizeObserver(true)
                .resizeObserverCenter(true)
                .resizable(false)
                .styleClass("max-w-screen")
                .iframeStyleClass("max-w-screen")
                .build();
        PrimeFaces.current().dialog().openDynamic("/resources/appelloeditor", options, null);
    }

    public void deleteDialog(Appello app){
        session.setSelectedAppello(app); //MAY USE PROTOTYPE
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .fitViewport(true)
                .responsive(true)
                .width("450px")
                .contentWidth("100%")
                .resizeObserver(true)
                .resizeObserverCenter(true)
                .resizable(true)
                .styleClass("max-w-screen")
                .iframeStyleClass("max-w-screen")
                .build();
        PrimeFaces.current().dialog().openDynamic("/resources/confirmdialog", options, null);
    }

    public void opzioniDialog(){
        Credentials cred = Credentials.newBuilder().setMat(session.getUsername()).setCf(session.getPassword()).build();
        credentials = new AuthCredentials(cred);
        Dto dto =requester.getBlockingStub().withCallCredentials(credentials).getOptions(Vuoto.newBuilder().build());
        CapsuleValidate c = assembler.disassembleValidate(dto);
        session.setOpzioni(c.getOptions()); //MAY USE PROTOTYPE
        session.setDeadline_h(TimeUnit.MILLISECONDS.toHours(session.getOpzioni().getDeadline_millis()));
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .fitViewport(true)
                .responsive(true)
                .width("900px")
                .contentWidth("100%")
                .resizeObserver(true)
                .resizeObserverCenter(true)
                .resizable(true)
                .styleClass("max-w-screen")
                .iframeStyleClass("max-w-screen")
                .build();
        PrimeFaces.current().dialog().openDynamic("/resources/optionEditor", options, null);
    }

    public void salva(){
        Credentials cred = Credentials.newBuilder().setMat(session.getUsername()).setCf(session.getPassword()).build();
        credentials = new AuthCredentials(cred);
        CapsuleValidate cap = new CapsuleValidate();
        cap.setModAppello(session.getSelectedAppello());
        Dto message = assembler.assemble(cap);
        Dto dto;
        if(session.isModificaAppello())
            dto = requester.getBlockingStub().withCallCredentials(credentials).modifica(message);
        else
            dto = requester.getBlockingStub().withCallCredentials(credentials).aggiungi(message);
        cap = assembler.disassembleValidate(dto);
        if (cap.getStatus()<0){
            FacesMessage dialog = new FacesMessage(FacesMessage.SEVERITY_WARN, "Errore nel salvataggio", cap.getException().getMessage());
            PrimeFaces.current().dialog().showMessageDynamic(dialog);
            return;
        }
        session.getAllAppellos().remove(session.getSelectedAppello());
        session.getAllAppellos().add(session.getSelectedAppello());
        session.getAllAppellos().sort((o1, o2) -> o2.getData_ora().compareTo(o1.getData_ora()));
        session.setSelectedAppello(new Appello());
        FacesMessage dialog = new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvataggio completato", "Appello correttamente inserito");
        PrimeFaces.current().dialog().showMessageDynamic(dialog);
    }

    public void delete(){
        Credentials cred = Credentials.newBuilder().setMat(session.getUsername()).setCf(session.getPassword()).build();
        credentials = new AuthCredentials(cred);
        AppelloID message = AppelloID.newBuilder().setId((Long) session.getSelectedAppello().getId()).build();
        Dto dto = requester.getBlockingStub().withCallCredentials(credentials).rimuovi(message);
        CapsuleValidate cap = assembler.disassembleValidate(dto);
        if (cap.getStatus()<0){
            FacesMessage dialog = new FacesMessage(FacesMessage.SEVERITY_WARN, "Errore nell'eliminazione", cap.getException().getMessage());
            PrimeFaces.current().dialog().showMessageDynamic(dialog);
            return;
        }
        session.getAllAppellos().remove(session.getSelectedAppello());
        session.setSelectedAppello(new Appello());
        FacesMessage dialog = new FacesMessage(FacesMessage.SEVERITY_INFO, "Appello eliminato correttamente", "Appello correttamente eliminato");
        PrimeFaces.current().dialog().showMessageDynamic(dialog);
    }

    public void saveOptions(){
        Options op = session.getOpzioni();
        op.setDeadline_millis(TimeUnit.HOURS.toMillis(session.getDeadline_h()));
        Credentials cred = Credentials.newBuilder().setMat(session.getUsername()).setCf(session.getPassword()).build();
        credentials = new AuthCredentials(cred);
        CapsuleValidate cap = new CapsuleValidate();
        cap.setOptions(op);
        Dto message = assembler.assemble(cap);
        Dto dto = requester.getBlockingStub().withCallCredentials(credentials).setOptions(message);
        cap = assembler.disassembleValidate(dto);
        if (cap.getStatus()<0){
            FacesMessage dialog = new FacesMessage(FacesMessage.SEVERITY_WARN, "Errore nella modifica", cap.getException().getMessage());
            PrimeFaces.current().dialog().showMessageDynamic(dialog);
            return;
        }
        FacesMessage dialog = new FacesMessage(FacesMessage.SEVERITY_INFO, "Impostazioni salvate", "Impostazioni correttamente salvate");
        PrimeFaces.current().dialog().showMessageDynamic(dialog);
    }



    private boolean checkInserimento() {
        return !(
                session.getPassword() == null || session.getPassword().isEmpty()
                || session.getUsername() == null || session.getUsername().isEmpty()
                );
    }

    public void saluto(){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", " Always Bet on Prime!");
        PrimeFaces.current().dialog().showMessageDynamic(message);
    }

    public boolean isCorretta(Risposta r){
        System.out.println(r);
        if (r == null) return false;

        return  r.isCorretta();
    }

    public void addRisposta(Domanda d){
        d.getRisposte().add(new Risposta());
    }

    public void addDomanda(){
        session.getSelectedAppello().getDomande().add(new Domanda());
    }

}
