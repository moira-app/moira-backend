package com.org.server.config;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.bulk.ClientBulkWriteOptions;
import com.mongodb.client.model.bulk.ClientBulkWriteResult;
import com.mongodb.client.model.bulk.ClientNamespacedWriteModel;
import com.mongodb.connection.ClusterDescription;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.mongodb.uri}")
    private String uri;

    @Value("${spring.mongodb.database}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .readPreference(ReadPreference.secondaryPreferred())
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        return MongoClients.create(mongoClientSettings);
    }
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDatabaseFactory,
                                                       MongoMappingContext mongoMappingContext,
                                                       Jsr310Converters.LocalDateTimeToDateConverter
                                                                   dateKstParser,
                                                       Jsr310Converters.DateToLocalDateTimeConverter
                                                                   localDateTimeKstConverter){

        DbRefResolver dbRefResolver=new DefaultDbRefResolver(mongoDatabaseFactory());
        MappingMongoConverter converter=new MappingMongoConverter(dbRefResolver,mongoMappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.setCustomConversions(new MongoCustomConversions(List.of(dateKstParser,localDateTimeKstConverter)));
        return converter;

    }
    @Bean
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactory(){
        return new SimpleMongoClientDatabaseFactory(mongoClient(),
                databaseName);
    }
    //@transaction 하에서 mongotemplate는 read시 primary노드로부터 데이터를 읽어오게된다.aggregate도 primary노드로부터 읽게만든다.
    /*@Bean(name="mongoTransactionManager")
    public MongoTransactionManager mongoTransactionManager() {
        TransactionOptions transactionOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.ACKNOWLEDGED)
                .build();
        return new MongoTransactionManager(mongoDatabaseFactory(), transactionOptions);
    }*/
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory());
    }

}