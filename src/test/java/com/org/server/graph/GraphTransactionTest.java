package com.org.server.graph;

import com.org.server.graph.domain.*;
import com.org.server.graph.domain.Properties;
import com.org.server.support.IntegralTestEnv;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class GraphTransactionTest extends IntegralTestEnv {


    @Autowired
    MongoTemplate mongoTemplate;


    LocalDateTime startTime;
    Root root;
    List<Graph> graphs=new ArrayList<>();
    String rootID= UUID.randomUUID().toString();
    @BeforeEach
    void settingBeforeTest(){
        startTime=LocalDateTime.now();
        root=new Root(rootID, startTime.toString(),1L,"root");
        root=graphRepository.save(root);
        Map<String, Properties> propertiesMap=new HashMap<>();
        Properties properties=new Properties("0",startTime.toString());
        propertiesMap.put("data",properties);
        Element pages =new Element(UUID.randomUUID().toString(),
                    root.getId(),propertiesMap,startTime.toString(),1L);
        graphRepository.save(pages);
        graphs.add(pages);
    }


    @DisplayName("트랜잭션 효과 테스트 failcountdownlatch가 1이여야됨.")
    @Test
    void testingGraphTransaction()throws InterruptedException{
        ExecutorService executorService= Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch=new CountDownLatch(5);
        CountDownLatch checkFailLatch=new CountDownLatch(5);

        LocalDateTime now=LocalDateTime.now().plusDays(1L);

        for(int i=0;5>i;i++){
            executorService.submit(()->{
                try{
                    Element e=(Element)(graphRepository.findById(graphs.get(0).getId()).get());
                    Long val=Long.parseLong(e.getProperties()
                            .get("data").getValue());
                    PropertiesUpdateDto propertiesUpdateDto=new PropertiesUpdateDto(
                            e.getId(),now,"data",String.valueOf(val+countDownLatch.getCount())
                    );
                    graphService.updateProperties(propertiesUpdateDto);
                }
                catch (Exception e){
                    checkFailLatch.countDown();
                }
                finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        Assertions.assertThat(checkFailLatch.getCount()).isEqualTo(1);
    }

    @DisplayName("트랜잭션이 없으므로 fail 카운트 다운이 5여야된다.")
    @Test
    void withOutGraphTransaction() throws InterruptedException{
        ExecutorService executorService= Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch=new CountDownLatch(5);
        CountDownLatch checkFailLatch=new CountDownLatch(5);
        LocalDateTime now=LocalDateTime.now();
        for(int i=0;5>i;i++){
            int val=i;
            executorService.submit(()->{
                try{
                    Element e=(Element)(graphRepository.findById(graphs.get(0).getId()).get());
                    Properties properties= e.getProperties().get("data");
                    if(now.isBefore(LocalDateTime.parse(properties.getModifyDate()))
                            ||now.isEqual(LocalDateTime.parse(properties.getModifyDate()))){
                        throw new RuntimeException();
                    }
                    properties.updateValue(String.valueOf(val));
                    properties.updateModifyDate(now.toString());
                    Query query=new Query(where("_id").is(e.getId()));
                    Update updateData=new Update().set("properties",e.getProperties());
                    mongoTemplate.updateFirst(query,updateData,Element.class);
                }
                catch (Exception err){
                    checkFailLatch.countDown();
                }
                finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        Assertions.assertThat(checkFailLatch.getCount()).isEqualTo(5);
    }

}
