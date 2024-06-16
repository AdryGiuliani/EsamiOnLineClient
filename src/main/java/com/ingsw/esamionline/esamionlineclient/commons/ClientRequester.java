package com.ingsw.esamionline.esamionlineclient.commons;

import application.services.ServerEsamiOnLine;
import gen.javaproto.FrontendServicesGrpc;
import gen.javaproto.FrontendServicesGrpc.FrontendServicesBlockingStub;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.io.Serializable;


@Named
@SessionScoped
public class ClientRequester  implements Serializable,  Requester<FrontendServicesGrpc.FrontendServicesBlockingStub>{

    private FrontendServicesBlockingStub blockingStub = FrontendServicesGrpc.newBlockingStub(GrpcConnection.getInstance());
    @Override
    public FrontendServicesBlockingStub getBlockingStub() {
        return blockingStub;
    }
}
