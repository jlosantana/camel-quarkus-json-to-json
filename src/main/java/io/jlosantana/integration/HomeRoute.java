package io.jlosantana.integration;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class HomeRoute extends RouteBuilder {

    @ConfigProperty(name = "routes.rest-path")
    String restPath;

    @ConfigProperty(name = "routes.source-url")
    String sourceUrl;

    @ConfigProperty(name = "routes.transform-spec-url")
    String transformSpecUrl;

    @ConfigProperty(name = "routes.transformer")
    String transformer;

    @Override
    public void configure() {
        restConfiguration()
                .component("platform-http")
                .bindingMode(RestBindingMode.json);

        rest(restPath)
                .get()
                .to("direct:external");

        from("direct:external")
            .to(sourceUrl + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json()
                .toD(transformer + ":" + transformSpecUrl);
    }
}
