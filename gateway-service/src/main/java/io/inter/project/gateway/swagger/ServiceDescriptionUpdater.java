package io.inter.project.gateway.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class ServiceDescriptionUpdater {

    private static final String DEFAULT_SWAGGER_URL = "/v2/api-docs";
    private static final String KEY_SWAGGER_URL = "swagger_url";


    private final DiscoveryClient discoveryClient;

    private final SwaggerUtils swaggerUtils;

    private final ServiceDefinitionsContext definitionContext;

    private final RestTemplate template;

    @Autowired
    public ServiceDescriptionUpdater(DiscoveryClient discoveryClient, SwaggerUtils swaggerUtils,
                                     ServiceDefinitionsContext definitionContext) {
        this.discoveryClient = discoveryClient;
        this.swaggerUtils = swaggerUtils;
        this.definitionContext = definitionContext;
        this.template = new RestTemplate();
    }


    @Scheduled(fixedDelayString = "${swagger.config.refreshrate}")
    public void refreshSwaggerConfigurations() {
        log.debug("Starting Service Definition Context refresh");
        discoveryClient.getServices().forEach(this::refreshServiceDefinition);
    }

    private void refreshServiceDefinition(String serviceId) {
        log.debug("Attempting service definition refresh for Service: {}", serviceId);
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
        if (serviceInstances == null || serviceInstances.isEmpty()) {
            log.info("No instances available for service: {}", serviceId);
        } else {
            ServiceInstance instance = serviceInstances.get(0);
            String swaggerURL = getSwaggerURL(instance);
            Optional<Object> jsonData = getSwaggerDefinitionForAPI(serviceId, swaggerURL);

            if (jsonData.isPresent()) {
                String content = getJSON(jsonData.get());
                updateServiceDefinition(serviceId, content);
            } else {
                log.error("Skipping service id: {}. Error: Could not get Swagger definition from API", serviceId);
            }
            log.info("Service Definition Context Refreshed at: {}", LocalDateTime.now());
        }
    }

    private String getSwaggerURL(ServiceInstance instance) {
        String swaggerURL = instance.getMetadata().get(KEY_SWAGGER_URL);
        return swaggerURL != null ? instance.getUri() + swaggerURL : instance.getUri() + DEFAULT_SWAGGER_URL;
    }

    private Optional<Object> getSwaggerDefinitionForAPI(String serviceName, String url) {
        log.debug("Accessing the SwaggerDefinition JSON for Service: {}. URL: {}", serviceName, url);
        try {
            Object jsonData = template.getForObject(url, Object.class);
            return Optional.ofNullable(jsonData);
        } catch (RestClientException ex) {
            log.error("Error while getting service definition for service: {}. Error: {}", serviceName, ex.getMessage());
            return Optional.empty();
        }
    }

    private void updateServiceDefinition(String serviceId, String content) {
        try {
            definitionContext.addServiceDefinition(serviceId, swaggerUtils.setUpdatedMappings(serviceId, content));
        } catch (JsonProcessingException e) {
            log.error("Error: {}", e.getMessage());
            definitionContext.addServiceDefinition(serviceId, "No data found for API definition");
        }
    }

    public String getJSON(Object jsonData) {
        try {
            String json = new ObjectMapper().writeValueAsString(jsonData);
            Swagger swagger = new SwaggerParser().parse(json);
            return Json.mapper().writeValueAsString(swaggerUtils.setSpecialDataIntoApiDocs(swagger));
        } catch (JsonProcessingException e) {
            log.error("Error: {}", e.getMessage());
            return "No data found for API definition";
        }
    }

}
