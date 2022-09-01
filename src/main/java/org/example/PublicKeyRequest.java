package org.example;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public class PublicKeyRequest {

    private static final Supplier<RuntimeException> NO_CERTIFICATE_FOUND_EXCEPTION = () -> new PublicKeyRequestException(new IllegalStateException("No certificate found"));

    /**
     * Requests the public key of the given host and returns it as an array of bytes.
     *
     * @param host Host to obtain the public key from.
     * @return Public key as array of bytes.
     */
    public byte[] get(final String host) {
        try {
            final var uri = URI.create(host);
            final var httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            final var httpResponse = HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.discarding());

            return httpResponse.sslSession().flatMap(this::getCertificate)
                    .map(this::getEncoded)
                    .orElseThrow(NO_CERTIFICATE_FOUND_EXCEPTION);
        } catch (final IOException | InterruptedException exception) {
            throw new PublicKeyRequestException(exception);
        }
    }

    private Optional<X509Certificate> getCertificate(final SSLSession sslSession) {
        try {
            final var peerCertificates = sslSession.getPeerCertificates();

            return Arrays.stream(peerCertificates).findFirst()
                    .map(X509Certificate.class::cast);
        } catch (final SSLPeerUnverifiedException sslPeerUnverifiedException) {
            throw new PublicKeyRequestException(sslPeerUnverifiedException);
        }
    }

    private byte[] getEncoded(final X509Certificate x509Certificate) {
        try {
            return x509Certificate.getEncoded();
        } catch (final CertificateEncodingException certificateEncodingException) {
            throw new PublicKeyRequestException(certificateEncodingException);
        }
    }
}
