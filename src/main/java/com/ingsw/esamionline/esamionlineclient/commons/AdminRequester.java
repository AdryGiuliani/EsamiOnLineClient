package com.ingsw.esamionline.esamionlineclient.commons;


import application.services.AdminServices;
import gen.javaproto.AdminServicesGrpc;
import gen.javaproto.FrontendServicesGrpc;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class AdminRequester implements Serializable, Requester<AdminServicesGrpc.AdminServicesBlockingStub> {
    private AdminServicesGrpc.AdminServicesBlockingStub blockingStub = AdminServicesGrpc.newBlockingStub(GrpcConnection.getInstance());

    @Override
    public AdminServicesGrpc.AdminServicesBlockingStub getBlockingStub() {
        return blockingStub;
    }
}
