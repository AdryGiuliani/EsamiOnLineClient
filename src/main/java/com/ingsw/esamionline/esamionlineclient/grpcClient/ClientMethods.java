package com.ingsw.esamionline.esamionlineclient.grpcClient;

import application.App;
import application.persistance.pojos.Appello;
import application.services.CapsuleDtoAssembler;
import application.validation.chainsteps.CapsuleValidate;
import com.ingsw.esamionline.esamionlineclient.StudenteSession;
import com.ingsw.esamionline.esamionlineclient.commons.AuthCredentials;
import com.ingsw.esamionline.esamionlineclient.commons.Navigator;
import com.ingsw.esamionline.esamionlineclient.commons.ClientRequester;
import com.ingsw.esamionline.esamionlineclient.util.Formatter;
import gen.javaproto.AppelloID;
import gen.javaproto.Credentials;
import gen.javaproto.Dto;
import jakarta.el.MethodExpression;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import jakarta.servlet.http.HttpSession;
import org.primefaces.model.DialogFrameworkOptions;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Named
@SessionScoped
public class ClientMethods implements Serializable {

    @Inject
    private StudenteSession session;
    @Inject
    private Navigator navigator;

    @Inject
    private ClientRequester requester;

    private AuthCredentials credentials;

    private CapsuleDtoAssembler assembler = new CapsuleDtoAssembler();

    public String formatDate(Date date) {
        return  Formatter.dateFormatter(date);
    }

    public String login(){
        if (!checkInserimento()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Login", "Inserisci username e password!");
            PrimeFaces.current().dialog().showMessageDynamic(message);
            return navigator.passlogin(false);
        }
        Credentials cred = Credentials.newBuilder().setMat(session.getMat()).setCf(session.getCf()).build();
        credentials = new AuthCredentials(cred);
        Dto dto = requester.getBlockingStub().withCallCredentials(credentials).login(cred);
        CapsuleValidate cap = new CapsuleDtoAssembler().disassembleValidate(dto);
        if (cap.getStatus()>=0){
            session.setStudent(cap.getStudent());
            session.setDisponibili(cap.getDisponibili());
            session.setDeadline(cap.getDeadline());
            return navigator.passlogin(true);
        }
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenziali non valide", cap.getException().getMessage());
        PrimeFaces.current().dialog().showMessageDynamic(message);
        return navigator.passlogin(false);
    }

    public String logout(){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Logout in corso", "Arrivederci");
        PrimeFaces.current().dialog().showMessageDynamic(message);
        String out = navigator.logout();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.invalidate();
        return out;
    }

    public void prenota(Appello a){
        Credentials cred = Credentials.newBuilder().setMat(session.getMat()).setCf(session.getCf()).build();
        credentials = new AuthCredentials(cred);
        AppelloID appelloID = AppelloID.newBuilder().setId((Long)a.getId()).build();
        Dto dto = requester.getBlockingStub().withCallCredentials(credentials).prenota(appelloID);
        CapsuleValidate cap = new CapsuleDtoAssembler().disassembleValidate(dto);
        if (cap.getStatus()>=0){
            session.getStudent().getPrenotazioni().add(a);
            session.getDisponibili().remove(a);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Prenotazione effettuata",
                    "Appello correttamente prenotato, potrai partecipare dalla sezione \"Prenotati\" il giorno dell'appello");
            PrimeFaces.current().dialog().showMessageDynamic(message);
        }
        if (cap.getException() == null)
            return;
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Impossibile prenotare", cap.getException().getMessage());
        PrimeFaces.current().dialog().showMessageDynamic(message);
    }

    public void delete_prenotazione(){
        Credentials cred = Credentials.newBuilder().setMat(session.getMat()).setCf(session.getCf()).build();
        credentials = new AuthCredentials(cred);
        AppelloID appid = AppelloID.newBuilder().setId((long)session.getAppelloselezionato().getId()).build();
        Dto dto = requester.getBlockingStub().withCallCredentials(credentials).cancella(appid);
        CapsuleValidate cap = assembler.disassembleValidate(dto);
        if (cap.getStatus()>=0){
            session.getStudent().getPrenotazioni().remove(session.getAppelloselezionato());
            session.getDisponibili().add(session.getAppelloselezionato());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Prenotazione cancellata",
                    "Prenotazione correttamente eliminata, puoi riprenotarti dalla pagina \"Disponibili\"");
            PrimeFaces.current().dialog().showMessageDynamic(message);
        }
        if (cap.getException() == null)
            return;
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Impossibile cancellare prenotazione", cap.getException().getMessage());
        PrimeFaces.current().dialog().showMessageDynamic(message);
    }
    private boolean checkInserimento() {
        // Logic to handle form submission
        return !(
                session.getMat() == null ||
                session.getMat().isEmpty() ||
                session.getCf() == null ||
                session.getCf().isEmpty()
        );
//       else {
//            // Proceed with form submission logic
//            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Form submitted successfully!");
//            PrimeFaces.current().dialog().showMessageDynamic(message);
//        }
    }

    public boolean deadlineCheck(Date data){
        return System.currentTimeMillis() + session.getDeadline() <= data.getTime();
    }

    public boolean partecipaCheck(Appello app){
        long now = System.currentTimeMillis();
        return now >= app.getData_ora().getTime() && now < app.getData_ora().getTime()+ TimeUnit.MINUTES.toMillis(app.getDurata_minuti());
    }

    public void rispostePopup(Appello appello) {
        if (!session.getAppelloselezionato().equals(appello)){
            Credentials cred = Credentials.newBuilder().setMat(session.getMat()).setCf(session.getCf()).build();
            credentials = new AuthCredentials(cred);
            AppelloID appelloID = AppelloID.newBuilder().setId((Long)appello.getId()).build();
            Dto dto = requester.getBlockingStub().withCallCredentials(credentials).getFullAppello(appelloID);
            CapsuleValidate c = assembler.disassembleValidate(dto);
            if (c.getStatus()<0){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Errore",
                        c.getException().getMessage());
                PrimeFaces.current().dialog().showMessageDynamic(message);
                return;
            }
            session.setAppelloselezionato(c.getAppelloCreato());
        }
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
            PrimeFaces.current().dialog().openDynamic("/resources/viewrisposte_stud", options, null);
    }

    public void elimina_prenotazione(Appello app){
        session.setAppelloselezionato(app);
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
        PrimeFaces.current().dialog().openDynamic("/resources/confirmdialog_prenotazione", options, null);
    }

    public void partecipaDialog(Appello appello) {
        if (!session.getAppelloselezionato().equals(appello)){
            Credentials cred = Credentials.newBuilder().setMat(session.getMat()).setCf(session.getCf()).build();
            credentials = new AuthCredentials(cred);
            AppelloID appelloID = AppelloID.newBuilder().setId((Long)appello.getId()).build();
            Dto dto = requester.getBlockingStub().withCallCredentials(credentials).getFullAppello(appelloID);
            CapsuleValidate c = assembler.disassembleValidate(dto);
            if (c.getStatus()<0){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Errore",
                        c.getException().getMessage());
                PrimeFaces.current().dialog().showMessageDynamic(message);
                return;
            }
            session.setAppelloselezionato(c.getAppelloCreato());
        }

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
        PrimeFaces.current().dialog().openDynamic("/resources/preview_partecipazione", options, null);
    }

}