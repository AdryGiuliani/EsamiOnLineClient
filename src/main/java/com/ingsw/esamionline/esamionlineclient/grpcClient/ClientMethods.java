package com.ingsw.esamionline.esamionlineclient.grpcClient;

import application.persistance.pojos.Appello;
import application.persistance.pojos.Risultato;
import application.services.CapsuleDtoAssembler;
import application.validation.chainsteps.CapsuleValidate;
import com.ingsw.esamionline.esamionlineclient.AppelloSession;
import com.ingsw.esamionline.esamionlineclient.StudenteSession;
import com.ingsw.esamionline.esamionlineclient.commons.AuthCredentials;
import com.ingsw.esamionline.esamionlineclient.commons.Navigator;
import com.ingsw.esamionline.esamionlineclient.commons.ClientRequester;
import com.ingsw.esamionline.esamionlineclient.util.Formatter;
import gen.javaproto.AppelloID;
import gen.javaproto.CompletedAppello;
import gen.javaproto.Credentials;
import gen.javaproto.Dto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import jakarta.servlet.http.HttpSession;
import org.primefaces.model.DialogFrameworkOptions;

import java.io.IOException;
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
    @Inject
    private AppelloSession sessione_esame;

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
            session.setSoglia(cap.getSoglia());
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
        setCredenziali();
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
        session.setDisponibili(cap.getDisponibili());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Impossibile prenotare", cap.getException().getMessage());
        PrimeFaces.current().dialog().showMessageDynamic(message);
    }

    public String delete_prenotazione(){
        setCredenziali();
        AppelloID appid = AppelloID.newBuilder().setId((long)session.getAppelloselezionato().getId()).build();
        Dto dto = requester.getBlockingStub().withCallCredentials(credentials).cancella(appid);
        CapsuleValidate cap = assembler.disassembleValidate(dto);
        if (cap.getStatus()>=0){
            session.getStudent().getPrenotazioni().remove(session.getAppelloselezionato());
            session.getDisponibili().add(session.getAppelloselezionato());
            session.setAppelloselezionato(null);
            PrimeFaces.current().executeScript("PF('dlg_del_prenotazione').hide();");
        }
        if (cap.getException() == null)
            return "";
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Impossibile cancellare prenotazione", cap.getException().getMessage());
        PrimeFaces.current().dialog().showMessageDynamic(message);
        return "";
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
        if (!get_full_appello(appello)) return;
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

    public void elimina_prenotazione_dialog(Appello app){
        session.setAppelloselezionato(app);
        PrimeFaces.current().executeScript("PF('dlg_del_prenotazione').show();");
    }

    public void partecipaDialog(Appello appello) {
        if (!get_full_appello(appello)) return;
        PrimeFaces.current().executeScript("PF('dlg').show();");
    }


    /**
     * Recupera le informazioni complete sull'appello selezionato (se non già recuperate)
     * (necessario perchè nel server Appello ha alcuni parametri lazy loaded)
     * @param appello
     * @return
     */
    private boolean get_full_appello(Appello appello) {
        if (session.getAppelloselezionato()==null || !session.getAppelloselezionato().equals(appello)){
            setCredenziali();
            AppelloID appelloID = AppelloID.newBuilder().setId((Long)appello.getId()).build();
            Dto dto = requester.getBlockingStub().withCallCredentials(credentials).getFullAppello(appelloID);
            CapsuleValidate c = assembler.disassembleValidate(dto);
            if (c.getStatus()<0){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Errore",
                        c.getException().getMessage());
                PrimeFaces.current().dialog().showMessageDynamic(message);
                return false;
            }
            session.setAppelloselezionato(c.getAppelloCreato());
        }
        return true;
    }

    public String partecipa(){
        setCredenziali();
        AppelloID appelloID = AppelloID.newBuilder().setId((Long) session.getAppelloselezionato().getId()).build();
        Dto dto = requester.getBlockingStub().withCallCredentials(credentials).partecipa(appelloID);
        CapsuleValidate cap = assembler.disassembleValidate(dto);
        System.out.println("capsula>" + cap.getStatus());
        if (cap.getStatus()>=0){
            sessione_esame.inizializzaEsame();
            System.out.println("Sto per redirect");
            return navigator.goto_esame(true);
        }
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Partecipazione non valida", cap.getException().getMessage());
        PrimeFaces.current().dialog().showMessageDynamic(message);
        return navigator.goto_esame(false);
    }

    public void terminaAppello(){
        setCredenziali();
        sessione_esame.calcolapunteggio();
        Risultato result = new Risultato();
        result.setPunteggio(sessione_esame.getPunteggio());
        result.setSuperato(sessione_esame.getPunteggio()>= session.getSoglia());
        result.setCompleted_appello(session.getAppelloselezionato());
        CompletedAppello appcompleto = CompletedAppello.newBuilder()
                .setIdAppello((Long) session.getAppelloselezionato().getId())
                .setPunteggio(sessione_esame.getPunteggio())
                .build();
        Dto dto  = requester.getBlockingStub().withCallCredentials(credentials).concludi(appcompleto);
        CapsuleValidate cap = assembler.disassembleValidate(dto);
        if (cap.getStatus()>=0){
            session.getStudent().getCompletato().add(result);
            session.setResultCorrente(result);
            System.out.println("Sto per redirect");
            try{
                navigator.goto_prenotati_static();
            }catch (IOException e){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Errore nella redirezione", "Tranquillo, il risultato è stato salvato correttamente.");
                PrimeFaces.current().dialog().showMessageDynamic(message);
            }
            session.setMostraResult(true);
            return;
        }
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Errore nel salvataggio dell'appello", cap.getException().getMessage());
        PrimeFaces.current().dialog().showMessageDynamic(message);
    }

    private void setCredenziali(){
        Credentials cred = Credentials.newBuilder().setMat(session.getMat()).setCf(session.getCf()).build();
        credentials = new AuthCredentials(cred);
    }

}