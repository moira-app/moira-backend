package com.org.server.graph;


import com.org.server.graph.domain.*;
import com.org.server.graph.domain.Properties;
import com.org.server.graph.dto.NodeDelDto;
import com.org.server.graph.dto.PropertyChangeDto;
import com.org.server.graph.dto.StructureChangeDto;
import com.org.server.support.IntegralTestEnv;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class GraphUpdateTest extends IntegralTestEnv {


    Root root;
    List<Graph> graphs=new ArrayList<>();
    String rootID= UUID.randomUUID().toString();
    @BeforeEach
    void settingBeforeTest(){
        root=new Root(rootID, LocalDateTime.now().toString(),1L,"root");
        root=graphRepository.save(root);
        for(int i=0;10>i;i++){

            Map<String, Properties> propertiesMap=new HashMap<>();
            for(int j=0;3>j;j++){
                Properties properties=new Properties("Test",LocalDateTime.now());
                propertiesMap.put(i+"-"+j,properties);
            }
            Element pages = graphs.isEmpty() ? new Element(UUID.randomUUID().toString(),
                    root.getId(),propertiesMap,LocalDateTime.now().toString(),1L) :
                    new Element(UUID.randomUUID().toString(),
                            graphs.get(i-1).getId(),propertiesMap,LocalDateTime.now().toString(),null);

            graphs.add(pages);
            graphRepository.save(pages);
        }
    }

    @Test
    @DisplayName("트리구조 수정 테스트 및 노드 속성 업데이트 테스트")
    void updateTest(){
        PropertyChangeDto propertiesUpdateDto=PropertyChangeDto.builder()
                .nodeId(graphs.get(0).getId())
                .name("0-0")
                .modifyDate(LocalDateTime.now())
                .graphActionType(GraphActionType.Property)
                .value("testing")
                .build();
        graphService.updateProperties(propertiesUpdateDto);
        Element e=(Element) graphRepository.findById(graphs.get(0).getId()).get();
        Properties properties=e.getProperties().get("0-0");
        Assertions.assertThat(properties.getValue()).isEqualTo("testing");

        //순환고리 탐지
        Assertions.assertThat(
                    graphService.updateNodeReference(
                            StructureChangeDto.builder()
                                    .graphActionType(GraphActionType.Structure)
                                    .nodeId(graphs.getFirst().getId())
                                    .parentId(graphs.getLast().getId())
                                    .rootId("rootId")
                                    .projectId(1L)
                                    .build())
        ).isEqualTo(false);


        //순환고리 통과, 정상수정.
        Assertions.assertThat(
        graphService.updateNodeReference(
                StructureChangeDto.builder()
                        .graphActionType(GraphActionType.Structure)
                        .nodeId(graphs.getLast().getId())
                        .parentId(graphs.getFirst().getId())
                        .rootId("rootId")
                        .projectId(1L)
                        .build())).isEqualTo(true);
        e=(Element) graphRepository.findById(graphs.getLast().getId()).get();
        Assertions.assertThat(e.getParentId()).isEqualTo(graphs.getFirst().getId());

        //삭제시 불려오는게 없는기 검증 즉 root node 아래에 딸려오는게없는지 검증.
        NodeDelDto nodeDelDto= NodeDelDto.builder()
                .rootId(rootID)
                .nodeId(graphs.getFirst().getId())
                .graphActionType(GraphActionType.Delete)
                .build();
        graphService.delGraphNode(nodeDelDto);
        Map<String, List<Graph>> maps=graphService.getWholeGraph(rootID);
        Assertions.assertThat(maps.keySet().size()).isEqualTo(0);


    }

    @Test
    @DisplayName("속성 수정시에 이미 수정된 시간보다 이전의 수정 내역이 들어오면 거부함.")
    void lateUpdateTest(){
        LocalDateTime modifyDate=LocalDateTime.now().minusDays(1L);

        PropertyChangeDto propertiesUpdateDto=PropertyChangeDto.builder()
                .nodeId(graphs.get(0).getId())
                .name("0-0")
                .modifyDate(modifyDate)
                .graphActionType(GraphActionType.Property)
                .value(String.valueOf("xvcvcvc"))
                .rootId("rootId")
                .projectId(1L)
                .build();
        Assertions.assertThat(graphService.updateProperties(propertiesUpdateDto)).isEqualTo(false);
    }

    @Test
    @DisplayName("삭제 진행시 같은 노드에 대한 삭제를진행, 이미삭제된 노드는 삭제를 불가하게만듬")
    void deletetest() throws InterruptedException {
        System.out.printf("테스트 코드 시작\n");
        NodeDelDto nodeDelDto = NodeDelDto.builder()
                .rootId(rootID)
                .nodeId(graphs.getFirst().getId())
                .graphActionType(GraphActionType.Delete)
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        CountDownLatch checkFailLatch = new CountDownLatch(3);
        for (int i = 0; 3> i; i++) {
            executorService.submit(()->{
            try{
                if(!graphService.delGraphNode(nodeDelDto)){
                    throw new RuntimeException();
                }
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
        Assertions.assertThat(checkFailLatch.getCount()).isEqualTo(1L);
    }

}