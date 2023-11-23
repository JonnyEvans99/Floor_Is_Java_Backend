package org.kainos.ea.api;

import javassist.NotFoundException;
import org.kainos.ea.cli.Job;
import org.kainos.ea.db.DatabaseConnector;
import org.kainos.ea.db.JobDao;
import org.kainos.ea.exception.FailedToGetJobsException;
import org.kainos.ea.exception.FailedtoDeleteException;

import java.sql.SQLException;
import java.util.List;

public class JobService {

    private final JobDao jobDao;
    private final DatabaseConnector databaseConnector;

    public JobService(JobDao jobDao, DatabaseConnector databaseConnector) {
        this.jobDao = jobDao;
        this.databaseConnector = databaseConnector;
    }

    public List<Job> getAllJobs() throws FailedToGetJobsException {

        try {
            return jobDao.getAllJobs(databaseConnector.getConnection());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new FailedToGetJobsException();
        }

    }

    public Job getById(int id) throws FailedToGetJobsException {
        try {
            return jobDao.getJobById(databaseConnector.getConnection(),id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new FailedToGetJobsException();
        }
    }

    public void delete(int id) throws FailedtoDeleteException, NotFoundException {
        try {

            Job job = jobDao.getJobById(databaseConnector.getConnection(), id);
            if (job == null) {
                System.err.println("Attempted to delete job by id " + id + " but it doesn't exist!");
                throw new NotFoundException("Could not find job by id " + id);
            }

            jobDao.deleteJob(databaseConnector.getConnection(), id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new FailedtoDeleteException("Failed to delete job!");
        }
    }
}
