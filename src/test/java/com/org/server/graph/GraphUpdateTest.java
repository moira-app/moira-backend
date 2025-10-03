package com.org.server.graph;

import com.org.server.exception.MoiraException;
import com.org.server.graph.domain.*;
import com.org.server.graph.domain.Properties;
import com.org.server.support.IntegralTestEnv;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

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
    void updateTest(){
        PropertiesUpdateDto propertiesUpdateDto=PropertiesUpdateDto.builder()
                .nodeId(graphs.get(0).getId())
                .modifyDate(LocalDateTime.now())
                .name("0-0")
                .value("testing")
                .build();
        graphService.updateProperties(propertiesUpdateDto);
        Element e=(Element) graphRepository.findById(graphs.get(0).getId()).get();
        Properties properties=e.getProperties().get("0-0");
        Assertions.assertThat(properties.getValue()).isEqualTo("testing");

        Assertions.assertThatThrownBy(()->{
                    graphService.updateNodeReference(graphs.getFirst().getId(),graphs.getLast().getId());
                }).isInstanceOf(MoiraException.class)
                .hasMessage("순환고리를 만들순없습니다.");
        graphService.updateNodeReference(graphs.getLast().getId(),graphs.getFirst().getId());
        e=(Element) graphRepository.findById(graphs.getLast().getId()).get();
        Assertions.assertThat(e.getParentId()).isEqualTo(graphs.getFirst().getId());

        graphService.delGraphNode(graphs.getFirst().getId());

        Map<String, List<Graph>> maps=graphService.getWholeGraph(rootID);
        Assertions.assertThat(maps.keySet().size()).isEqualTo(0);


    }
    @Test
    @DisplayName("속성 수정시에 이미 수정된 시간보다 이전의 수정 내역이 들어오면 거부함.")
    void lateUpdateTest(){
        LocalDateTime modifyDate=LocalDateTime.now().minusDays(1L);

        PropertiesUpdateDto propertiesUpdateDto=new PropertiesUpdateDto(
                graphs.get(0).getId(),
                modifyDate,
                "0-0",
                "xvcvcvc");

        Assertions.assertThatThrownBy(()->{
            graphService.updateProperties(propertiesUpdateDto);
        }).isInstanceOf(MoiraException.class)
                .hasMessage("업데이트를 할수없습니다");
    }
}