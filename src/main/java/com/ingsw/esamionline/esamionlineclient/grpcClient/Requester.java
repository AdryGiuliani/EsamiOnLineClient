package com.ingsw.esamionline.esamionlineclient.grpcClient;

import application.TestDB;
import application.persistance.SessionCreator;
import application.persistance.util.Utils;
import application.services.ServerEsamiOnLine;
import gen.javaproto.FrontendServicesGrpc;
import gen.javaproto.FrontendServicesGrpc.FrontendServicesBlockingStub;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import jakarta.annotation.ManagedBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class Requester {
    private ManagedChannel channel;
    private FrontendServicesBlockingStub blockingStub;

    public FrontendServicesBlockingStub getBlockingStub() {
        return blockingStub;
    }

    public void startChannel(){
        if (channel == null || channel.isShutdown()) {
            String target = "localhost:" + ServerEsamiOnLine.port;
            channel= Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
            blockingStub = FrontendServicesGrpc.newBlockingStub(channel);
        }
    }

    public void stopChannel(){
        if (!channel.isShutdown()) {
            channel.shutdown();
        }
    }

    private Requester() {
       startChannel();
    }

    public static Requester getInstance() {return Requester.RequesterHelper.instance;}

    private static class RequesterHelper {
        private static final Requester instance = new Requester();
        private RequesterHelper() {}
    }

}
