package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
public class Handler implements RequestHandler<Handler.Parameter, String> {

    @Data
    public static class Parameter {

        @Data
        public static final class Input {

            private String host;
        }

        @Data
        public static final class Output {

            private String region;

            private String bucket;

            private String key;
        }

        private Input input;

        private Output output;
    }

    private final PublicKeyRequest publicKeyRequest = new PublicKeyRequest();

    @Override
    public String handleRequest(final Handler.Parameter parameter, final Context context) {
        try {
            final var bytes = this.publicKeyRequest.get(parameter.input.host);
            final var region = Region.of(parameter.output.region);

            try (final var s3Client = S3Client.builder()
                    .region(region)
                    .build()) {
                final var putObjectRequest = PutObjectRequest.builder()
                        .bucket(parameter.output.bucket)
                        .key(parameter.output.key)
                        .build();
                final var requestBody = RequestBody.fromBytes(bytes);
                s3Client.putObject(putObjectRequest, requestBody);
            }
        } catch (final PublicKeyRequestException publicKeyRequestException) {
            log.error("", publicKeyRequestException);

            return "400 BAD REQUEST";
        }

        return "200 OK";
    }
}