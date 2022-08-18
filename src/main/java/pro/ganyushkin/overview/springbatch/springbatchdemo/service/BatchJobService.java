package pro.ganyushkin.overview.springbatch.springbatchdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchJobService {
    private static final String FIND_JOB_STATUS =
        "SELECT \"status\" FROM \"arxiv-snapshot\".\"public\".\"batch_job_execution\" WHERE \"job_execution_id\" = :id";
    private final JobLauncher asyncJobLauncher;
    private final Job arxivImporterJob;
    private final Job arxivGenerationJob;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Long startBatchProcessing(String inputFilePath) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        log.debug("start batch processing for inputFilePath=" + inputFilePath);

        var builder = new JobParametersBuilder();
        builder.addDate("timestamp", new Date());
        builder.addString("filePath", inputFilePath);

        var exception = asyncJobLauncher.run(arxivImporterJob, builder.toJobParameters());

        log.info("batch job was started with jobId=" + exception.getJobId());

        return exception.getJobId();
    }

    public String getJobStatus(Integer id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(FIND_JOB_STATUS, namedParameters, String.class);
    }

    public Long startArticleGeneration() throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        log.debug("process raw data and generate articles");

        var builder = new JobParametersBuilder();
        builder.addDate("timestamp", new Date());

        var exception = asyncJobLauncher.run(arxivGenerationJob, builder.toJobParameters());

        log.info("batch job was started with jobId=" + exception.getJobId());

        return exception.getJobId();
    }
}
