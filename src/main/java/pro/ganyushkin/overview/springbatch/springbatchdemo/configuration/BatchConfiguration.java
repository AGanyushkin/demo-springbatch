package pro.ganyushkin.overview.springbatch.springbatchdemo.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import pro.ganyushkin.overview.springbatch.springbatchdemo.batch.RawArxivEntryProcessor;
import pro.ganyushkin.overview.springbatch.springbatchdemo.batch.ArxivArticleEntryProcessor;
import pro.ganyushkin.overview.springbatch.springbatchdemo.domain.ArxivArticleEntry;
import pro.ganyushkin.overview.springbatch.springbatchdemo.domain.RawArxivEntry;
import pro.ganyushkin.overview.springbatch.springbatchdemo.repository.ArxivArticleEntryRepository;
import pro.ganyushkin.overview.springbatch.springbatchdemo.repository.RawArxivEntryRepository;

import javax.sql.DataSource;
import java.net.MalformedURLException;

/**
 * @see "https://docs.spring.io/spring-batch/docs/4.1.x/reference/html/job.html#configuringAJob"
 * @see "https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html"
 * @see "https://docs.spring.io/spring-batch/docs/1.1.x/apidocs/org/springframework/batch/item/file/FlatFileItemReader.html"
 */
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {
    private static final int BATCH_SIZE = 13;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobRepository jobRepository;

    @Bean
    @JobScope
    public ItemReader<String> arxivSnapshorFileReader(@Value("#{jobParameters['filePath']}") String inputFilePath)
            throws MalformedURLException {
        var reader = new FlatFileItemReader<String>();
        reader.setResource(new FileUrlResource(inputFilePath));
        reader.setLineMapper(new PassThroughLineMapper());
        reader.open(new ExecutionContext());
        return reader;
    }

    @Bean
    public ItemWriter<RawArxivEntry> rawArxivEntryWriter(RawArxivEntryRepository snapshotArticleRepository) {
        var writer = new RepositoryItemWriter<RawArxivEntry>();
        writer.setRepository(snapshotArticleRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public ItemReader<RawArxivEntry> rawArxivEntryReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<RawArxivEntry>()
                .name("RawArxivEntryItemReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM raw_arxiv_entry")
                .rowMapper(new BeanPropertyRowMapper<>(RawArxivEntry.class))
                .build();
    }

    @Bean
    public ItemWriter<ArxivArticleEntry> ArxivArticleWriter(ArxivArticleEntryRepository finalDataEntryRepository) {
        var writer = new RepositoryItemWriter<ArxivArticleEntry>();
        writer.setRepository(finalDataEntryRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step importerFromSnapshotFile(ItemReader<String> arxivSnapshorReader,
                                         ItemWriter<RawArxivEntry> rawArxivEntryWriter,
                                         RawArxivEntryProcessor snapshotItemProcessor) {
        return stepBuilderFactory
                .get("import file to raw entries")
                .<String, RawArxivEntry>chunk(BATCH_SIZE)
                .reader(arxivSnapshorReader)
                .processor(snapshotItemProcessor)
                .writer(rawArxivEntryWriter)
                .build();
    }

    @Bean
    public Step generateArxivArticles(ItemReader<RawArxivEntry> rawArxivEntryReader,
                                      ItemWriter<ArxivArticleEntry> ArxivArticleWriter,
                                      ArxivArticleEntryProcessor finalDataItemProcessor) {
        return stepBuilderFactory
                .get("generate arxiv articles from raw")
                .<RawArxivEntry, ArxivArticleEntry>chunk(BATCH_SIZE)
                .reader(rawArxivEntryReader)
                .processor(finalDataItemProcessor)
                .writer(ArxivArticleWriter)
                .build();
    }

    @Bean
    public Job arxivImporterJob(@Qualifier("importerFromSnapshotFile") Step step1,
                                @Qualifier("generateArxivArticles") Step step2) {
        return jobBuilderFactory.get("import data from arxiv snapshot")
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Job arxivGenerationJob(@Qualifier("generateArxivArticles") Step generateArticlesFromRawData) {
        return jobBuilderFactory.get("generate articles from imported raw data")
                .start(generateArticlesFromRawData)
                .build();
    }

    @Bean
    public TaskExecutor jobExecutor() {
        return new SimpleAsyncTaskExecutor("job-");
    }

    @Bean
    public JobLauncher asyncJobLauncher(TaskExecutor jobExecutor) {
        var jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(jobExecutor);
        return jobLauncher;
    }
}
