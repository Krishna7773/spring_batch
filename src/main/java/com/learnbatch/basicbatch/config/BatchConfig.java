package com.learnbatch.basicbatch.config;

import com.learnbatch.basicbatch.entities.Customer;
import com.learnbatch.basicbatch.repositories.CustomerRepo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
  private   PlatformTransactionManager transactionManager;

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private CustomerRepo customerRepo;

    @Bean
    public FlatFileItemReader<Customer> itemReader()
    {
    FlatFileItemReader<Customer> itemReader= new FlatFileItemReader<>();
    itemReader.setResource(new FileSystemResource("src/main/resources/Customer.csv"));
    itemReader.setName("csvReader");
    itemReader.setLinesToSkip(1);
    itemReader.setLineMapper(lineMapper());
    return itemReader;
    }

    @Bean
    public ItemProcessor<Customer,Customer> processor()
    {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> writer()
    {

        RepositoryItemWriter<Customer> writer=new RepositoryItemWriter<>();
        writer.setRepository(customerRepo);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step()
    {
       return new StepBuilder("basicBatch",jobRepository)
               .<Customer,Customer>chunk(10,transactionManager)
               .reader(itemReader())
               .processor(processor())
               .writer(writer())
               .build();
    }

    @Bean
    public Job job()
    {
        return new JobBuilder("basicbatch",jobRepository)
                .start(step())
                .build();
    }

    public LineMapper<Customer> lineMapper()
    {
        DefaultLineMapper<Customer> lineMapper=new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","firstName","lastName","product");

        BeanWrapperFieldSetMapper<Customer> setMapper=new BeanWrapperFieldSetMapper<>();
        setMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(setMapper);

        return lineMapper;
    }
}
