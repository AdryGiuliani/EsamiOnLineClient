package com.ingsw.esamionline.esamionlineclient.commons;

import application.services.ServerEsamiOnLine;
import gen.javaproto.FrontendServicesGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractStub;

public interface Requester<S extends AbstractStub<S>>{

        public  S getBlockingStub();
    }
