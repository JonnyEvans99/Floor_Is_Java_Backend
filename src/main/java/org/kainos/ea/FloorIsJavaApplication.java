package org.kainos.ea;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.kainos.ea.resources.JobController;
import org.kainos.ea.resources.TestController;

public final class FloorIsJavaApplication
        extends Application<FloorIsJavaConfiguration> {
    /**
     * Main class method.
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        new FloorIsJavaApplication().run(args);
    }

    @Override
    public String getName() {
        return "floorIsJava";
    }

    @Override
    public void initialize(
            final Bootstrap<FloorIsJavaConfiguration> bootstrap) {
    }

    @Override
    public void run(final FloorIsJavaConfiguration configuration,
                    final Environment environment) {
        environment.jersey().register(new TestController());
        environment.jersey().register(new JobController());
    }

}
