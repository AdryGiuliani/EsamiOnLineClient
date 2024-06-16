package com.ingsw.esamionline.esamionlineclient.grpcClient;

import application.services.CapsuleDtoAssembler;
import application.validation.chainsteps.CapsuleValidate;
import com.ingsw.esamionline.esamionlineclient.StudenteSession;
import com.ingsw.esamionline.esamionlineclient.commons.AuthCredentials;
import com.ingsw.esamionline.esamionlineclient.commons.Navigator;
import com.ingsw.esamionline.esamionlineclient.commons.ClientRequester;
import com.ingsw.esamionline.esamionlineclient.util.Formatter;
import gen.javaproto.Credentials;
import gen.javaproto.Dto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.Date;


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
        session.setRequestStatus(cap.getStatus());
        if (cap.getStatus()>=0){
            session.clearStatus();
            session.setStudent(cap.getStudent());
            session.setDisponibili(cap.getDisponibili());
            return navigator.passlogin(true);
        }
        session.setException(cap.getException());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenziali non valide", session.getException().getMessage());
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
}