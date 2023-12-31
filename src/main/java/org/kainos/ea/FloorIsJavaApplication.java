package org.kainos.ea;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.kainos.ea.api.JobService;
import org.kainos.ea.core.JobValidator;
import org.kainos.ea.db.DatabaseConnector;
import org.kainos.ea.db.JobDao;
import org.kainos.ea.resources.CapabilityController;
import org.kainos.ea.resources.JobBandController;
import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;
import org.kainos.ea.api.AuthService;
import org.kainos.ea.api.IAuthService;
import org.kainos.ea.auth.JwtGenerator;
import org.kainos.ea.auth.JwtValidator;
import org.kainos.ea.db.AuthDao;
import org.kainos.ea.resources.AuthorisationFilter;
import org.kainos.ea.resources.AuthController;
import org.kainos.ea.resources.JobController;
import org.kainos.ea.resources.ResponsibilityController;

@SwaggerDefinition(
    securityDefinition = @SecurityDefinition(
        apiKeyAuthDefinitions = {
            @ApiKeyAuthDefinition(
                key = "basicAuth",
                name = "Authorization",
                in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER)
        }
    )
)
public class FloorIsJavaApplication extends Application<FloorIsJavaConfiguration> {

    private final IAuthService authService = new AuthService(new AuthDao(), new JwtGenerator(),new JwtValidator());

    public static void main(final String[] args) throws Exception {
        new FloorIsJavaApplication().run(args);
    }

    @Override
    public String getName() {
        return "floorIsJava";
    }

    @Override
    public void initialize(final Bootstrap<FloorIsJavaConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<FloorIsJavaConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(FloorIsJavaConfiguration configuration) {
                return configuration.getSwagger();
            }
        });
    }

    @Override
    public void run(final FloorIsJavaConfiguration configuration,
                    final Environment environment) {
        JobService jobService = new JobService(new JobDao(),new DatabaseConnector(),new JobValidator());
        environment.jersey().register(new AuthController(authService));
        environment.jersey().register(new AuthorisationFilter(authService));
        environment.jersey().register(new JobController(jobService));
        environment.jersey().register(new CapabilityController());
        environment.jersey().register(new JobBandController());
        environment.jersey().register(new ResponsibilityController());
    }
}
