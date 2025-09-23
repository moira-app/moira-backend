package com.org.server.certification.service;


import com.org.server.certification.repository.ProjectCertRepo;
import com.org.server.exception.MoiraException;
import com.org.server.util.MediaType;
import com.org.server.whiteBoardAndPage.domain.Page;
import com.org.server.whiteBoardAndPage.domain.PageDto;
import com.org.server.whiteBoardAndPage.domain.WhiteBoard;
import com.org.server.whiteBoardAndPage.repository.PageRepo;
import com.org.server.whiteBoardAndPage.repository.WhiteBoardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectPageS3Service {

    private final PageRepo pageRepo;
    private final S3Service s3Service;
    private final ProjectCertRepo projectCertRepo;


    public List<PageDto> getPageList(Long projectId) {
        Long boardId = projectCertRepo.getWhiteBoardId(projectId);
        List<Page> pages = pageRepo.findByWhiteBoardId(boardId);
        return pages.stream()
                .map(x -> {
                    if(x.getFileLocation()==null){
                        return null;
                    }
                    return new PageDto(s3Service.getPagePreSignUrl(x.getFileLocation()),
                            x.getId());
                })
                .collect(Collectors.toList());
    }

    //조회용 url
    public String getPageUrl(Long pageId) {
        Optional<Page> page = pageRepo.findById(pageId);

        if (page.isEmpty()) {
            throw new MoiraException("없는 페이지입니다", HttpStatus.BAD_REQUEST);
        }
        String fileLocation = page.get().getFileLocation();
        if(fileLocation==null){
            throw new MoiraException("아직 저장이 안된 자료입니다", HttpStatus.BAD_REQUEST);
        }
        return s3Service.getPagePreSignUrl(fileLocation);
    }

    //update용url
    public String updatePageUrl(Long pageId){
        Optional<Page> page=pageRepo.findById(pageId);
        if(page.isEmpty()){
            throw new MoiraException("존재 하지 않는 페이지입니다",HttpStatus.BAD_REQUEST);
        }
        if(page.get().getFileLocation()==null){
            return s3Service.savePagePreSignUrl(MediaType.MEDIA_WHITE_BOARD.getValue(),
                    page.get().getPageName());
        }

        return s3Service.updatePagePreSignUrl(MediaType.MEDIA_WHITE_BOARD.getValue(),
                page.get().getFileLocation());
    }
    public Long savePage(Long projectId,Long pageId,String fileLocation,String fileName){
        if(pageId==null&&fileLocation==null){
            Page p=Page.builder()
                    .pageName(fileName)
                    .whiteBoardId(projectCertRepo.getWhiteBoardId(projectId))
                    .build();
            p=pageRepo.save(p);
            return p.getId();
        }
        Optional<Page> p = pageRepo.findById(pageId);
        if(p.isPresent()&&!fileName.equals(p.get().getPageName())) {
            p.get().updatePageName(fileName);
        }
        if(p.get().getFileLocation()==null&&fileLocation!=null){
            p.get().updateFileLocation(fileLocation);
        }
        return p.get().getId();
    }
    public void delPage(Long pageId){
        Optional<Page> page=pageRepo.findById(pageId);
        if(page.get().getFileLocation()!=null) {
            s3Service.delPagePreSignUrl(page.get().getFileLocation());
        }
    }
}