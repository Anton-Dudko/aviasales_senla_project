package io.inter.project.gateway.swagger;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.HeaderParameter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SwaggerUtils {

    public static final String GUEST_ROUTE_PARAM = "guest";
    public static final String PARAM_NAME = "Authorization";
    public static final String DESCRIPTION = "Bearer token";
    public static final String CONTENT_TYPE = "string";
    public static final String HOST_VALUE = "localhost:8080";

    public Swagger setSpecialDataIntoApiDocs(Swagger swagger) {
        for (Map.Entry<String, Path> entry : swagger.getPaths().entrySet()) {
            String path = entry.getKey();
            Path pathObject = entry.getValue();
            if (!path.contains(GUEST_ROUTE_PARAM)) {
                for (Operation operation : pathObject.getOperations()) {
                    HeaderParameter authHeader = new HeaderParameter();
                    authHeader.setName(PARAM_NAME);
                    authHeader.setDescription(DESCRIPTION);
                    authHeader.setRequired(true);
                    authHeader.setType(CONTENT_TYPE);

                    operation.addParameter(authHeader);
                }
            }
        }

        swagger.setHost(HOST_VALUE);
        return swagger;
    }

}
