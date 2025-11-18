package com.org.server.graph;

import com.mongodb.client.result.UpdateResult;
import com.org.server.exception.MoiraException;
import com.org.server.graph.domain.Element;
import com.org.server.graph.domain.Graph;
import com.org.server.graph.domain.Properties;
import com.org.server.graph.domain.Root;
import com.org.server.graph.dto.PropertyChangeDto;
import com.org.server.support.IntegralTestEnv;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class TestMongoTransaction extends IntegralTestEnv {


    @Autowired
    MongoTemplate mongoTemplate;
    Root root;

    Element e;
    String rootID= UUID.randomUUID().toString();
    @BeforeEach
    void settingBeforeTest(){
        root=new Root(rootID, LocalDateTime.now().toString(),1L,"root");
        root=graphRepository.save(root);
        Map<String, Properties> propertiesMap=new HashMap<>();
        for(int j=0;5>j;j++){
            Properties properties=new Properties("Test",LocalDateTime.now());
            propertiesMap.put(0+"-"+j,properties);
        }
        e =new Element(UUID.randomUUID().toString(),
                root.getId(),propertiesMap,LocalDateTime.now().toString(),1L);
        graphRepository.save(e);

    }

    @DisplayName("한 객체의 각각 다른프로퍼티 수정시 충돌이 일어나는가 테스트-결론은 안일어남.")
    @Test
    void withOutGraphTransaction() throws InterruptedException{
        ExecutorService executorService= Executors.newFixedThreadPool(5);
        CountDownLatch countFailLatch=new CountDownLatch(5);
        CountDownLatch countDownLatch=new CountDownLatch(5);

        for(int i=0;5>i;i++){
            int val=i;
            executorService.submit(()->{
                try{
                    LocalDateTime now=LocalDateTime.now();
                    PropertyChangeDto propertiesUpdateDto=PropertyChangeDto.builder()
                            .nodeId(e.getId())
                            .name("0-"+val)
                            .modifyDate(now)
                            .graphActionType(GraphActionType.Property)
                            .value("changed")
                            .rootId("rootId")
                            .projectId(1L)
                            .build();

                    if(!graphService.updateProperties(propertiesUpdateDto)){
                        throw new RuntimeException();
                    }
                }
                catch (Exception e){
                    countFailLatch.countDown();
                }
                finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();
        executorService.shutdown();
        Element el=(Element) graphRepository.findById(e.getId()).get();
        Assertions.assertThat(countFailLatch.getCount()).isEqualTo(5);
        List<Properties> vals=el.getProperties().values().stream()
                .collect(Collectors.toList());
        Assertions.assertThat(vals).extracting("value")
                .contains("changed");
    }

    @DisplayName("한 객체의 같은 프로퍼티 동시수정.-충돌이 발생해서 1개만된다.")
    @Test
    void testupdating() throws InterruptedException{
        ExecutorService executorService= Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch=new CountDownLatch(5);
        CountDownLatch countFailLatch=new CountDownLatch(5);
        for(int i=0;5>i;i++){
            executorService.submit(()->{
                try{
                    LocalDateTime now=LocalDateTime.now();
                    PropertyChangeDto propertiesUpdateDto=PropertyChangeDto.builder()
                            .nodeId(e.getId())
                            .name("0-0")
                            .modifyDate(now)
                            .graphActionType(GraphActionType.Property)
                            .value("changed")
                            .rootId("rootId")
                            .projectId(1L)
                            .build();

                    if(!graphService.updateProperties(propertiesUpdateDto)){
                        throw new RuntimeException();
                    }

                }
                catch (Exception e){
                    countFailLatch.countDown();
                }
                finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();
        executorService.shutdown();
        Assertions.assertThat(countFailLatch.getCount()).isEqualTo(1);
    }



}
