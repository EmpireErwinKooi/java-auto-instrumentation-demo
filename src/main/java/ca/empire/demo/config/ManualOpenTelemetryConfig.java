package ca.empire.demo.config;


import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.OpenTelemetrySdkBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;


@Configuration
@ConditionalOnProperty(name="openTelemetry", havingValue="manual")
public class ManualOpenTelemetryConfig {
    static final Logger LOGGER = Logger.getGlobal();

    @Bean
    public OpenTelemetry getOpenTelemetry() {
        LOGGER.info("ManualOpenTelemetryConfig is active: " + System.getenv("OTEL_SERVICE_NAME"));

        Resource providerResource = Resource.getDefault().merge(Resource.create(
                Attributes.builder()
                        .put("application", System.getenv("OTEL_SERVICE_NAMESPACE"))
                        .put("service.name", System.getenv("OTEL_SERVICE_NAME"))
                        .put("service.namespace", System.getenv("OTEL_SERVICE_NAMESPACE"))
                        .put("service.version", System.getenv("OTEL_SERVICE_VERSION"))
                        .put("deployment.environment", System.getenv("OTEL_RESOURCE_ATTRIBUTES_DEPLOYMENT_ENVIRONMENT"))
                        .build())
        );

        OpenTelemetrySdkBuilder builder = OpenTelemetrySdk
                .builder()
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()));


        LOGGER.info("OTEL_EXPORTER_OTLP_ENDPOINT: "+System.getenv("OTEL_EXPORTER_OTLP_ENDPOINT"));
        builder.setTracerProvider(SdkTracerProvider
                .builder()
                .setSampler(Sampler.alwaysOn())
                .addSpanProcessor(BatchSpanProcessor.builder(
                        OtlpGrpcSpanExporter.builder()
                                .setEndpoint(System.getenv("OTEL_EXPORTER_OTLP_ENDPOINT"))
                                .build()
                ).build())
                .setResource(providerResource)
                .build());

        return builder.buildAndRegisterGlobal();
    }
}
