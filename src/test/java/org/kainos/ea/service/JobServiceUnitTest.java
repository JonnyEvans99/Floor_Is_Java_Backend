package org.kainos.ea.service;

import javassist.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kainos.ea.api.JobService;
import org.kainos.ea.cli.Job;
import org.kainos.ea.cli.JobRequest;
import org.kainos.ea.core.JobValidator;
import org.kainos.ea.db.DatabaseConnector;
import org.kainos.ea.db.JobDao;
import org.kainos.ea.exception.FailedToGetJobsException;
import org.kainos.ea.exception.ProjectException;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.kainos.ea.exception.FailedtoDeleteException;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
public class JobServiceUnitTest {


    JobDao jobDao = Mockito.mock(JobDao.class);
    DatabaseConnector databaseConnector = Mockito.mock(DatabaseConnector.class);
    JobValidator jobValidator = Mockito.mock(JobValidator.class);
    JobRequest jobRequest = new JobRequest(
            "Test",
            1,
            "This is a test",
            "https://google.com",
            1);

    JobService jobService = new JobService(jobDao, databaseConnector, jobValidator);
    Connection connection;

    @Test
    void getAllJobs_shouldReturnListOfJobs_whenDaoReturnsListOfJobs() throws SQLException, FailedToGetJobsException {

        // Create sample jobs and add them to a List
        Job sampleJob1 = new Job(1, "Software Engineer", "Engineering", "Develops, tests, and maintains software applications, collaborates with cross-functional teams, and follows best practices to deliver efficient and reliable software solutions.", "https://kainossoftwareltd.sharepoint.com/people/Job%20Specifications/Forms/AllItems.aspx?id=%2Fpeople%2FJob%20Specifications%2FEngineering%2FJob%20profile%20%2D%20Software%20Engineer%20%28Associate%29%2Epdf&parent=%2Fpeople%2FJob%20Specifications%2FEngineering&p=true&ga=1", "Trainee");
        Job sampleJob2 = new Job(2, "Test Engineer", "Quality Assurance", "Designs and executes test plans, identifies and reports software defects, and collaborates with development teams to ensure the delivery of reliable and high-quality software products.", "https://kainossoftwareltd.sharepoint.com/people/Job%20Specifications/Forms/AllItems.aspx?id=%2Fpeople%2FJob%20Specifications%2FEngineering%2FJob%20profile%20%20Test%20Engineer%20%28Associate%29%2Epdf&parent=%2Fpeople%2FJob%20Specifications%2FEngineering", "Trainee");
        Job sampleJob3 = new Job(3, "Senior Software Engineer", "Platforms", "Designs and develops complex software solutions, mentors junior engineers, and collaborates with cross-functional teams to deliver high-quality software products.", "https://kainossoftwareltd.sharepoint.com/people/Job%20Specifications/Forms/AllItems.aspx?id=%2Fpeople%2FJob%20Specifications%2FEngineering%2FJob%20profile%20%2D%20Senior%20Software%20Engieneer%20%28Senior%20Associate%29%2Epdf&parent=%2Fpeople%2FJob%20Specifications%2FEngineering&p=true&ga=1", "Senior Associate");

        List<Job> expectedResult = Arrays.asList(sampleJob1, sampleJob2, sampleJob3);

        // Test that the same list is returned from both the JobService and JobDao
        Mockito.when(databaseConnector.getConnection()).thenReturn(connection);
        Mockito.when(jobDao.getAllJobs(connection)).thenReturn(expectedResult);

        List<Job> result = jobService.getAllJobs();

        assertEquals(result, expectedResult);

    }

    @Test
    void getAllJobs_shouldThrowFailedToGetJobsException_whenDaoThrowsSQLException() throws SQLException {
        Mockito.when(databaseConnector.getConnection()).thenReturn(connection);
        Mockito.when(jobDao.getAllJobs(connection)).thenThrow(SQLException.class);

        assertThrows(FailedToGetJobsException.class,
                () -> jobService.getAllJobs());
    }

    @Test
    void getAllJobs_shouldThrowFailedToGetJobsException_whenDatabaseConnectorThrowsSQLException() throws SQLException {
        Mockito.when(databaseConnector.getConnection()).thenThrow(SQLException.class);
        Mockito.when(jobDao.getJobById(connection,-1)).thenReturn(null);

        assertThrows(FailedToGetJobsException.class,
                () -> jobService.getAllJobs());
    }

    @Test
    void createJobRole_shouldReturnNewJobId_whenValidInputIsProvided() throws SQLException, ProjectException {

        int expectedID = 1;

        Mockito.when(databaseConnector.getConnection()).thenReturn(connection);
        Mockito.when(jobDao.createJobRole(jobRequest, connection)).thenReturn(expectedID);

        int result = jobService.createJobRole(jobRequest);

        assertEquals(result, expectedID);
    }

    @Test
    void createJobRole_shouldThrowProjectException_whenValidationFails() throws SQLException {

        Mockito.when(databaseConnector.getConnection()).thenReturn(connection);
        Mockito.when(jobDao.createJobRole(jobRequest, connection)).thenThrow(SQLException.class);

        assertThrows(SQLException.class, ()-> {
            jobService.createJobRole(jobRequest);
        });
    }

    @Test
    void deleteJob_shouldThrowNotFoundException_IfJobIdNotFound() throws SQLException, FailedtoDeleteException {
        Mockito.when(databaseConnector.getConnection()).thenReturn(connection);
        Mockito.when(jobDao.getJobById(connection,-1)).thenReturn(null);
        Mockito.doNothing().when(jobDao).deleteJob(isA(Connection.class),isA(Integer.class));
        assertThrows(NotFoundException.class,() -> {
            jobService.delete(-1);
        });
    }

    @Test
    void deleteJob_shouldNotThrowAnything_IfJobIdFound() throws SQLException, FailedtoDeleteException, NotFoundException {
        Job sampleJob1 = new Job(1, "Software Engineer","Capbility", "Develops, tests, and maintains software applications, collaborates with cross-functional teams, and follows best practices to deliver efficient and reliable software solutions.", "https://kainossoftwareltd.sharepoint.com/people/Job%20Specifications/Forms/AllItems.aspx?id=%2Fpeople%2FJob%20Specifications%2FEngineering%2FJob%20profile%20%2D%20Software%20Engineer%20%28Associate%29%2Epdf&parent=%2Fpeople%2FJob%20Specifications%2FEngineering&p=true&ga=1","Trainee");
        Mockito.when(databaseConnector.getConnection()).thenReturn(connection);
        Mockito.when(jobDao.getJobById(connection,-1)).thenReturn(sampleJob1);
        Mockito.doNothing().when(jobDao).deleteJob(isA(Connection.class),isA(Integer.class));

        Assertions.assertDoesNotThrow(() -> {
            jobService.delete(-1);
        });
    }

    @Test
    void deleteJob_shouldThrowFailedToDeleteException_whenDAOthrowsSQLException() throws SQLException, FailedtoDeleteException {
        Mockito.when(databaseConnector.getConnection()).thenReturn(connection);
        Mockito.when(jobDao.getJobById(connection,-1)).thenThrow(SQLException.class);
        Mockito.doNothing().when(jobDao).deleteJob(isA(Connection.class),isA(Integer.class));

        assertThrows(FailedtoDeleteException.class,() -> {
            jobService.delete(-1);
        });
    }

    @Test
    void getJobById_shouldReturnValidJob_whenDAOReturnsJobById() throws SQLException, FailedToGetJobsException {
        Job sampleJob1 = new Job(1, "Software Engineer", "Engineering", "Develops, tests, and maintains software applications, collaborates with cross-functional teams, and follows best practices to deliver efficient and reliable software solutions.", "https://kainossoftwareltd.sharepoint.com/people/Job%20Specifications/Forms/AllItems.aspx?id=%2Fpeople%2FJob%20Specifications%2FEngineering%2FJob%20profile%20%2D%20Software%20Engineer%20%28Associate%29%2Epdf&parent=%2Fpeople%2FJob%20Specifications%2FEngineering&p=true&ga=1", "Trainee");

        Mockito.when(databaseConnector.getConnection()).thenReturn(connection);
        Mockito.when(jobDao.getJobById(connection,sampleJob1.getJobID())).thenReturn(sampleJob1);

        Job job = jobService.getById(sampleJob1.getJobID());

        assertEquals(sampleJob1.getJobID(),job.getJobID());
    }

    @Test
    void getJobById_shouldReturnNull_whenDAOReturnsNull() throws SQLException, FailedToGetJobsException {
        Mockito.when(databaseConnector.getConnection()).thenReturn(connection);
        Mockito.when(jobDao.getJobById(connection,-1)).thenReturn(null);

        Job job = jobService.getById(-1);

        assertNull(job);
    }

    @Test
    void getJobById_shouldThrowFailedToGetJobsException_whenDAOthrowsSQLException() throws SQLException {
        Mockito.when(databaseConnector.getConnection()).thenThrow(SQLException.class);

        assertThrows(FailedToGetJobsException.class,() -> {
            jobService.getById(-1);
        });
    }
}
