package com.org.server.whiteBoardAndPage.domain;


import com.org.server.util.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Page extends BaseTime {



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //1:다 관계
    private String pageName;
    private Long whiteBoardId;
    @Column(length=512)
    private String fileLocation;

    @Builder
    public Page(Long whiteBoardId, String pageName,String fileLocation) {
        this.whiteBoardId = whiteBoardId;
        this.fileLocation = fileLocation;
        this.pageName=pageName;
    }

    public void updatePageName(String pageName){
        this.pageName=pageName;
    }
    public void updateFileLocation(String fileLocation){
        this.fileLocation=fileLocation;
    }
}
