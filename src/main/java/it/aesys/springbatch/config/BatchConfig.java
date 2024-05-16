package it.aesys.springbatch.config;

import it.aesys.springbatch.job.PlayerItemProcessor;
import it.aesys.springbatch.model.Player;
import it.aesys.springbatch.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private JobBuilderFactory jobBuilderFactory;

    private PlayerRepository playerRepository;

    private StepBuilderFactory stepBuilderFactory;

    public BatchConfig(JobBuilderFactory jb, PlayerRepository pr,StepBuilderFactory sbf){
        this.jobBuilderFactory=jb;
        this.playerRepository=pr;
        this.stepBuilderFactory=sbf;
    }

    @Bean
    public FlatFileItemReader<Player> reader() {
        FlatFileItemReader<Player> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/players.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Player> lineMapper() {
        DefaultLineMapper<Player> lineMapper = new DefaultLineMapper();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstname", "lastname", "position", "team");

        BeanWrapperFieldSetMapper<Player> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(Player.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        return lineMapper;
    }

    @Bean
    public PlayerItemProcessor processor() {
        return new PlayerItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Player> writer() {
        RepositoryItemWriter<Player> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(playerRepository);
        itemWriter.setMethodName("save");
        return itemWriter;

    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("csv step").<Player, Player>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job runJob() {

        return jobBuilderFactory.get("importPlayers")
                .flow(step1())
                .end()
                .build();


    }
}
