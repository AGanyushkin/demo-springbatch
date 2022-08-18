package pro.ganyushkin.overview.springbatch.springbatchdemo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.ganyushkin.overview.springbatch.springbatchdemo.service.BatchJobService;

@Slf4j
@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class JobApi {
    private final BatchJobService batchJobService;

    @RequestMapping(path = "/job", method = RequestMethod.POST,
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public Long startProcessingForFile(@RequestBody String inputFilePath) {
        try {
            log.info("inputFilePath=" + inputFilePath);
            return batchJobService.startBatchProcessing(inputFilePath);
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                JobParametersInvalidException | JobRestartException e) {
            log.error("Can't execute job", e);
            return -1L;
        }
    }

    @RequestMapping(path = "/job/generate", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public Long startRawDataProcessing() {
        try {
            return batchJobService.startArticleGeneration();
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            log.error("Can't execute job", e);
            return -1L;
        }
    }

    @RequestMapping(path = "/job", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getJobStatus(@RequestParam Integer id) {
        return batchJobService.getJobStatus(id);
    }
}
