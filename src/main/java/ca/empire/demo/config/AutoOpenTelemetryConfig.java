package ca.empire.demo.config;


import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
@ConditionalOnProperty(name="openTelemetry", havingValue="auto")
public class AutoOpenTelemetryConfig {
    static final Logger LOGGER = Logger.getGlobal();

    @Bean
    public OpenTelemetry getOpenTelemetry() {
        LOGGER.info("AutoOpenTelemetryConfig is active");

        return AutoConfiguredOpenTelemetrySdk
                .builder()
                .addSamplerCustomizer((sampler, configProperties) -> Sampler.alwaysOn())
                .build()
                .getOpenTelemetrySdk();
    }
}
