package org.kainos.ea.db;

import org.kainos.ea.cli.Capability;
import org.kainos.ea.cli.JobBand;
import org.kainos.ea.exception.FailedToGetJobBandsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JobBandDao {

    public List<JobBand> getAllJobBands(Connection connection) throws FailedToGetJobBandsException {

        try {
            // Query the database for all capabilities
            String selectQuery = "SELECT job_band_id, job_band_name FROM job_band;";

            PreparedStatement statement = connection.prepareStatement(selectQuery);

            ResultSet results = statement.executeQuery();

            // Create a list of Capability objects from the results
            List<JobBand> allJobBands = new ArrayList<>();
            while (results.next()) {
                JobBand jobBand = new JobBand(
                        results.getInt("job_band_id"),
                        results.getString("job_band_name")
                );
                allJobBands.add(jobBand);
            }
            return allJobBands;
        } catch (SQLException e) {
            throw new FailedToGetJobBandsException();
        }
    }
}
