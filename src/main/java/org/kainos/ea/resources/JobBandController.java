package org.kainos.ea.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.kainos.ea.api.CapabilityService;
import org.kainos.ea.api.JobBandService;
import org.kainos.ea.cli.Authorised;
import org.kainos.ea.cli.UserRole;
import org.kainos.ea.db.CapabilityDao;
import org.kainos.ea.db.DatabaseConnector;
import org.kainos.ea.db.JobBandDao;
import org.kainos.ea.exception.FailedToGetCapabilitiesException;
import org.kainos.ea.exception.FailedToGetJobBandsException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Api(
    value = "Floor is Java Job API",
    authorizations = {@Authorization(value = "basicAuth")}
)
@Path("/api")
public class JobBandController {

    /**
     * instantiate CapabilityService class.
     */
    private final JobBandService jobBandService;
    /**
     * instantiate DatabaseConnector class.
     */
    private final DatabaseConnector databaseConnector;

    public JobBandController() {
        databaseConnector = new DatabaseConnector();
        jobBandService = new JobBandService(new JobBandDao(), databaseConnector);
    }

    public JobBandController(JobBandService jobBandService) {
        databaseConnector = new DatabaseConnector();
        this.jobBandService = jobBandService;
    }

    @GET
    @Path("/jobBands")
    @Produces(MediaType.APPLICATION_JSON)
    @Authorised({UserRole.Admin, UserRole.User})
    public Response getAllJobBands() {

        try {
            return Response.ok(jobBandService.getAllJobBands()).build();
        } catch (FailedToGetJobBandsException | SQLException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}