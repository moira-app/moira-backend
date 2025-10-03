package com.org.server.graph;


import com.org.server.graph.domain.*;
import com.org.server.graph.domain.Properties;
import com.org.server.support.IntegralTestEnv;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.util.*;

public class GraphTest extends IntegralTestEnv {

    Root root;
    List<Graph> graphs=new ArrayList<>();
    String rootID= UUID.randomUUID().toString();
    @BeforeEach
    void settingBeforeTest(){
        root=new Root(rootID,LocalDateTime.now().toString(),1L,"root");
        root=graphRepository.save(root);
        Root root2=new Root(UUID.randomUUID().toString()
                ,LocalDateTime.now().toString(),1L,"root");
        graphRepository.save(root2);

        for(int i=0;300>i;i++){

            Map<String,Properties> propertiesMap=new HashMap<>();
            for(int j=0;3>j;j++){
                Properties properties=new Properties("Test",LocalDateTime.now().toString());
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
    @DisplayName("그래프 전체 조회 테스트")
    void testingGraphSearch(){
        Map<String,List<Graph>> graphData=graphService.getWholeGraph(rootID);
        Assertions.assertThat(graphData.keySet().size()).isEqualTo(300);

        graphService.createElementNode(new ElementCreateDto(UUID.randomUUID().toString()
                ,graphs.getLast().getId(),Map.of()));

        graphData=graphService.getWholeGraph(rootID);
        Assertions.assertThat(graphData.keySet().size()).isEqualTo(301);

        graphService.delGraphNode(graphs.get(0).getId());

        graphData=graphService.getWholeGraph(rootID);
        Assertions.assertThat(graphData.keySet().size()).isEqualTo(0);


        graphService.delGraphNode(rootID);
        List<Graph> g=graphService.getRootNodes(1L);
        Assertions.assertThat(g.size()).isEqualTo(1);
    }
}