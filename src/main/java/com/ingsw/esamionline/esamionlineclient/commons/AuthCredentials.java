package com.ingsw.esamionline.esamionlineclient.commons;

import application.services.MyAuthInterceptor;
import gen.javaproto.Credentials;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;

import java.util.concurrent.Executor;

public class AuthCredentials extends CallCredentials {

    private Credentials credentials;
    public AuthCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
    void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
    /**
     * Pass the credential data to the given {@link MetadataApplier}, which will
     * propagate it to the request metadata.
     *
     * <p>It is called for each individual RPC, within the {@link}context of the call, before the
     * stream is about to be created on a transport. Implementations should not block in this
     * method. If metadata is not immediately available, e.g., needs to be fetched from network, the
     * implementation may give the {@code applier} to an asynchronous task which will eventually call
     * the {@code applier}. The RPC proceeds only after the {@code applier} is called.
     *
     * @param requestInfo request-related information
     * @param appExecutor The application thread-pool. It is provided to the implementation in case it
     *                    needs to perform blocking operations.
     * @param applier     The outlet of the produced headers. It can be called either before or after this
     *                    method returns.
     */
    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
        appExecutor.execute(() -> {
            try {
                Metadata headers = new Metadata();
                headers.put(MyAuthInterceptor.KEY_CREDENTIALS, credentials);
                applier.apply(headers);
            } catch (Throwable e) {
                applier.fail(Status.UNAUTHENTICATED.withCause(e));
            }
        });
    }
}
