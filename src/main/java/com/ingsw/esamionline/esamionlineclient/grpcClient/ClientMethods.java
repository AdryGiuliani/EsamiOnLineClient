package com.ingsw.esamionline.esamionlineclient.grpcClient;

import application.services.CapsuleDtoAssembler;
import application.validation.chainsteps.Capsule;
import application.validation.chainsteps.CapsuleValidate;
import com.ingsw.esamionline.esamionlineclient.StudenteSession;
import gen.javaproto.ClientGRPC;
import gen.javaproto.Credentials;
import gen.javaproto.Dto;
import gen.javaproto.FrontendServicesGrpc;
import io.grpc.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jdk.jshell.spi.ExecutionControl;

import java.io.Serializable;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Named
@SessionScoped
public class ClientMethods implements Serializable {

    @Inject
    private StudenteSession session;

    private static final Requester requester = Requester.getInstance();
    private AuthCredentials credentials;
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String LOGIN = "login";


    public void setSession(StudenteSession session) {
        this.session = session;
    }

    public String login() {
        Credentials cred = Credentials.newBuilder().setMat(session.getMat()).setCf(session.getCf()).build();
        credentials = new AuthCredentials(cred);
        Dto dto = requester.getBlockingStub().withCallCredentials(credentials).login(cred);
        CapsuleValidate cap = new CapsuleDtoAssembler().disassembleValidate(dto);
        session.setRequestStatus(cap.getStatus());
        if (cap.getStatus()<0){
            session.setException(cap.getException());
            return FAILURE;
        }
        session.clearStatus();
        session.setStudent(cap.getStudent());
        session.setDisponibili(cap.getDisponibili());
        return SUCCESS;
    }
}