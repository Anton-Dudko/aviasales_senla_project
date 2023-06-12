package io.inter.project.gateway.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.inter.project.gateway.configuration.GatewayConfig;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SwaggerUtils {

    public static final String GUEST_ROUTE_PARAM = "guest";
    public static final String USER_DETAILS_PARAM = "userDetails";
    public static final String PARAM_NAME = "Authorization";
    public static final String DESCRIPTION = "Bearer token";
    public static final String CONTENT_TYPE = "string";
    public static final String HOST_VALUE = "localhost:8080";



    private final GatewayConfig gatewayConfig;
    @Autowired
    public SwaggerUtils(GatewayConfig gatewayConfig) {
        this.gatewayConfig = gatewayConfig;
    }

    public Swagger setSpecialDataIntoApiDocs(Swagger swagger) {
        addAuthHeaderToOperations(swagger);
        setHostValue(swagger);

        return swagger;
    }

    public String setUpdatedMappings(String serviceName,
                                     String content) throws JsonProcessingException {
        Swagger swagger = new SwaggerParser().parse(content);
        if (gatewayConfig.getMappings().containsKey(serviceName)) {
            Map<String, Path> newPaths = createNewPaths(serviceName, swagger);
            swagger.setPaths(newPaths);
            List<Tag> tagList = filterTags(swagger.getTags());
            swagger.setTags(tagList);
        }

        return Json.mapper().writeValueAsString(swagger);
    }

    private void addAuthHeaderToOperations(Swagger swagger) {
        if (!MapUtils.isEmpty(swagger.getPaths())) {
            for (Map.Entry<String, Path> entry : swagger.getPaths().entrySet()) {
                String path = entry.getKey();
                Path pathObject = entry.getValue();
                if (!path.contains(GUEST_ROUTE_PARAM)) {
                    for (Operation operation : pathObject.getOperations()) {
                        operation.setParameters(operation.getParameters().stream()
                                .filter(x -> !x.getName().equals(USER_DETAILS_PARAM))
                                .collect(Collectors.toList()));
                        HeaderParameter authHeader = createAuthHeader();
                        operation.addParameter(authHeader);
                    }
                }
            }
        }
    }

    private HeaderParameter createAuthHeader() {
        HeaderParameter authHeader = new HeaderParameter();
        authHeader.setName(PARAM_NAME);
        authHeader.setDescription(DESCRIPTION);
        authHeader.setRequired(true);
        authHeader.setType(CONTENT_TYPE);

        return authHeader;
    }

    private void setHostValue(Swagger swagger) {
        swagger.setHost(HOST_VALUE);
    }

    private Map<String, Path> createNewPaths(String serviceName,
                                             Swagger swagger) {
        Map<String, Path> newPaths = swagger.getPaths();
        gatewayConfig.getMappings().get(serviceName).forEach((key, value) -> {
            String newValue = value.replace("$", "");
            String newKey = key.replace("**", "{id}");
            Path oldPath = swagger.getPaths().get(newValue);
            newPaths.remove(newValue, oldPath);
            newPaths.put(newKey, oldPath);

        });
        if (!CollectionUtils.isEmpty(gatewayConfig.getExcludedEndpointList())) {
            gatewayConfig.getExcludedEndpointList()
                    .forEach(newPaths::remove);
        }

        return newPaths;
    }

    private List<Tag> filterTags(List<Tag> tags) {
        List<String> exclusionList = gatewayConfig.getExclusionList();
        return tags.stream()
                .filter(x -> !exclusionList.contains(x.getName()))
                .collect(Collectors.toList());
    }

}
