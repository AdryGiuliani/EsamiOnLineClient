package com.ingsw.esamionline.esamionlineclient.commons;

import application.services.ServerEsamiOnLine;
import gen.javaproto.FrontendServicesGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

public class GrpcConnection {

    private static class GrpcConnectionHelper{
        private static final ManagedChannel instance = startChannel();
    }

    public static ManagedChannel getInstance(){
        return GrpcConnectionHelper.instance;
    }

    private static ManagedChannel startChannel(){
            String target = "localhost:" + ServerEsamiOnLine.port;
            return Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
    }

}
