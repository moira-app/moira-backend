package com.org.server.graph;

import com.org.server.exception.MoiraException;
import com.org.server.graph.domain.Element;
import com.org.server.graph.domain.Graph;
import com.org.server.graph.domain.Properties;
import com.org.server.graph.domain.Root;
import com.org.server.support.IntegralTestEnv;
import com.org.server.support.TestRedisConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
@ExtendWith({SpringExtension.class})
@Import({TestRedisConfig.class})
public class TestMongoTransaction extends IntegralTestEnv {


    @Autowired
    MongoTemplate mongoTemplate;
    Root root;

    Element e;
    List<Graph> graphs=new ArrayList<>();
    String rootID= UUID.randomUUID().toString();
    @BeforeEach
    void settingBeforeTest(){
        root=new Root(rootID, LocalDateTime.now().toString(),1L,"root");
        root=graphRepository.save(root);
        Map<String, Properties> propertiesMap=new HashMap<>();
        for(int j=0;5>j;j++){
            Properties properties=new Properties("Test",LocalDateTime.now().toString());
            propertiesMap.put(0+"-"+j,properties);
        }
        e =new Element(UUID.randomUUID().toString(),
                root.getId(),propertiesMap,LocalDateTime.now().toString(),1L);
        graphRepository.save(e);

    }

    @DisplayName("한 객체의 각각 다른프로퍼티 수정시 충돌이 일어나는가 테스트 결론은 안일어남.")
    @Test
    void withOutGraphTransaction() throws InterruptedException{
        ExecutorService executorService= Executors.newFixedThreadPool(5);

        CountDownLatch countDownLatch=new CountDownLatch(5);

        for(int i=0;5>i;i++){
            int val=i;
            executorService.submit(()->{
                try{

                    Properties properties= e.getProperties().getOrDefault(0+"-"+val,
                            null);
                    if(properties==null){
                        throw new MoiraException("없는 속성입니다", HttpStatus.BAD_REQUEST);
                    }

                    properties.updateValue("changed");
                    properties.updateModifyDate(LocalDateTime.now().toString());
                    Query query=new Query(where("_id").is(e.getId()));
                    Update updateData=new Update().set("properties",e.getProperties());
                    mongoTemplate.updateFirst(query,updateData,Element.class);
                }
                catch (Exception e){
                    System.out.println("에러");
                }
                finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();
        executorService.shutdown();
        Element el=(Element) graphRepository.findById(e.getId()).get();

        List<Properties> vals=el.getProperties().values().stream()
                .collect(Collectors.toList());
        Assertions.assertThat(vals).extracting("value")
                .contains("changed");
    }



}
