package org.kainos.ea.integration;

import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kainos.ea.FloorIsJavaApplication;
import org.kainos.ea.FloorIsJavaConfiguration;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kainos.ea.cli.JobRequest;
import org.kainos.ea.cli.Login;
import javax.ws.rs.client.Entity;
import org.kainos.ea.cli.Job;
import org.kainos.ea.cli.JobRequest;
import org.kainos.ea.db.DatabaseConnector;
import org.kainos.ea.db.JobDao;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class JobControllerIntegrationTest {

    private static final JobDao jobDao = new JobDao();

    static final DropwizardAppExtension<FloorIsJavaConfiguration> APP = new DropwizardAppExtension<>(
            FloorIsJavaApplication.class, null,
            new ResourceConfigurationSourceProvider()
    );

    private static final String VALID_USERNAME = System.getenv("TEST_VALID_ADMIN_USERNAME");
    private static final String VALID_PASSWORD = System.getenv("TEST_VALID_ADMIN_PASSWORD");

    private String getJWT() {
        if(VALID_USERNAME == null || VALID_PASSWORD == null){
            throw new IllegalArgumentException("Test credential environment variables not set!");
        }
        Login credentials = new Login(VALID_USERNAME,VALID_PASSWORD);
        Response response = APP.client().target("http://localhost:8080/api/login").request().post(Entity.json(credentials));

        return response.readEntity(String.class);
    }

    @Test
    void getAllJobs_ShouldReturnListOfJobs() {
        Response response = APP.client().target("http://localhost:8080/api/jobs")
                .request()
                .header("Authorization","Bearer " + getJWT())
                .accept(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());

        // Check that the all jobs in the response contain the correct properties
        List<Job> allJobs = response.readEntity(List.class);

        Assertions.assertFalse(allJobs.isEmpty());

    }

    @Test
    void createJob_ShouldReturnCreatedStatus() {
        // Create a new job object or use a predefined job object
        JobRequest newJob = new JobRequest("newJob",1,"A new job","job@newjob.com",2);

        // Send a POST request to create a job
        Response response = APP.client().target("http://localhost:8080/api/jobs")
                .request()
                .header("Authorization","Bearer " + getJWT())
                .post(Entity.json(newJob)); // Send the job as JSON in the request body

        assertEquals(200, response.getStatus());
    }

    @Test
    void createJob_WithInvalidData_ShouldReturnBadRequest() {
        // Create a new job object with invalid data or missing required fields
        JobRequest invalidJob = new JobRequest("", 0, "A new job", "job@newjob.com", 2);

        // Send a POST request to create a job with invalid data
        Response response = APP.client().target("http://localhost:8080/api/jobs")
                .request()
                .header("Authorization", "Bearer " + getJWT())
                .post(Entity.json(invalidJob)); // Send the invalid job as JSON in the request body

        // Assert that the HTTP response status code is 400 (Bad Request) or appropriate error code
        assertEquals(400, response.getStatus());
    }

    @Test
    void deleteJob_shouldDeleteJob() throws SQLException {
        JobRequest request = new JobRequest(
                "Test Job, to be deleted",
                1,
                "This job will be deleted",
                "www.thiswillbeeleted.com",
                1
        );

        int id = jobDao.createJobRole(request, new DatabaseConnector().getConnection());

        Response response = APP.client().target("http://localhost:8080/api/jobs/" + id)
            .request()
            .header("Authorization","Bearer " + getJWT())
            .accept(MediaType.APPLICATION_JSON)
            .delete();

        Assertions.assertEquals(200,response.getStatus());
    }

    @Test
    void deleteJob_shouldReturn404_ifIDNotFound() {
        int jobId = -1;
        Response response = APP.client().target("http://localhost:8080/api/jobs/" + jobId)
                .request()
                .header("Authorization","Bearer " + getJWT())
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        Assertions.assertEquals(404,response.getStatus());
    }

    @Test
    void deleteJob_shouldReturn400_ifBadIdPassed() {
        Response response = APP.client().target("http://localhost:8080/api/jobs/" + "This is definitely not a number")
                .request()
                .header("Authorization","Bearer " + getJWT())
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        Assertions.assertEquals(400,response.getStatus());
    }

    @Test
    void getJobById_shouldReturn200andJob_whenValidIdPassed() {
        Response response = APP.client().target("http://localhost:8080/api/job/" + "5")
                .request()
                .header("Authorization","Bearer " + getJWT())
                .accept(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200,response.getStatus());
        Assertions.assertNotNull(response.readEntity(Job.class));
    }

    @Test
    void getJobById_shouldReturn404_whenInvalidIdPassed() {
        Response response = APP.client().target("http://localhost:8080/api/job/" + "-1")
                .request()
                .header("Authorization","Bearer " + getJWT())
                .accept(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(404,response.getStatus());
        Assertions.assertNotNull(response.readEntity(Job.class));
    }

    @Test
    void getJobById_shouldReturn400_whenBadIdPassed() {
        Response response = APP.client().target("http://localhost:8080/api/job/" + "This is invalid")
                .request()
                .header("Authorization","Bearer " + getJWT())
                .accept(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(400,response.getStatus());
        Assertions.assertNotNull(response.readEntity(Job.class));
    }

    @Test
    void getJobById_shouldReturn400_whenInvalidIdPassed() {
        Response response = APP.client().target("http://localhost:8080/api/job/")
                .request()
                .header("Authorization","Bearer " + getJWT())
                .accept(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(404,response.getStatus());
        Assertions.assertNotNull(response.readEntity(Job.class));
    }
}
