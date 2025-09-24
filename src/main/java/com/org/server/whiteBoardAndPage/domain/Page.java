package com.org.server.whiteBoardAndPage.domain;


import com.org.server.util.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String fileLocation;
    private Boolean deleted;

    @Builder
    public Page(Long whiteBoardId, String pageName,String fileLocation) {
        this.whiteBoardId = whiteBoardId;
        this.fileLocation = fileLocation;
        this.pageName=pageName;
    }
    public void updateDeleted(){
        this.deleted=!deleted;
    }
}
