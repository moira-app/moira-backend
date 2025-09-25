package com.org.server.s3;


import com.org.server.member.domain.MemberImgUpdate;
import com.org.server.member.service.MemberServiceImpl;
import com.org.server.util.ApiResponseUtil;
import com.org.server.whiteBoardAndPage.domain.PageDto;
import com.org.server.whiteBoardAndPage.domain.PageSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final MemberServiceImpl memberService;
    private final ProjectPageS3Service projectPageS3Service;
    @PostMapping("/myImg/fin")
    public ResponseEntity<ApiResponseUtil<Object>>
    saveChangeMyImgUrl(MemberImgUpdate memberImgUpdate){
        memberService.updateMemberImg(memberImgUpdate.getId()
                ,memberImgUpdate.getFileName());

        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }

    // 특정 pageid값을 기준으로 해당 페이지를 불러오는게 실패했을떄 get url을 재발급 하는 ai.
    @GetMapping("/project/{projectId}/get/reUrl")
    public ResponseEntity<ApiResponseUtil<String>> getPageData(
            @RequestBody PageDto pageDto){
        return ResponseEntity.ok(
                ApiResponseUtil.CreateApiResponse("ok",
                        projectPageS3Service.getPageUrl(pageDto.getPageId())));
    }
    //처음에 미팅 입장시 해당 프로젝트와 연관된 page들의 get url들 뿌리기.
    @GetMapping("/project/{projectId}/get/pageUrls")
    public ResponseEntity<ApiResponseUtil<List<PageDto>>> getPageList(
            @PathVariable(name = "projectId") Long projectId
    ){return ResponseEntity.ok(
            ApiResponseUtil.CreateApiResponse("ok"
                    ,projectPageS3Service.getPageList(projectId)));}

    //이미 존재하는 page에 업데이트를 위해서 url을 발급받기.
    //이때 pagedto 에는 file
    @GetMapping("/project/{projectId}/update/pageUrl")
    public ResponseEntity<ApiResponseUtil<String>> updatePageUrl(
            @RequestBody PageDto pageDto
    ){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectPageS3Service.updatePageUrl(pageDto.getPageId())));
    }
    //page 저장 api
    //최초 page생성시 저장--> 이름만 넣어서 보내주면 리턴으로 pageid값 반환
    //이미 저장되었던 page저장시--> id값과 이름, 있다면은 filelocation넣어서 제공.
    @PostMapping("/project/{projectId}/save/page")
    public ResponseEntity<ApiResponseUtil<Long>> savePageData(
            @PathVariable(name = "projectId") Long proejectId,
            @RequestBody PageSaveDto pageSaveDto){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectPageS3Service.savePage(proejectId,pageSaveDto.getPageId(),
                        pageSaveDto.getFileLocation()
                        ,pageSaveDto.getFileName())));
    }
    @DeleteMapping("/project/{projectId}/del")
    public ResponseEntity<ApiResponseUtil<String>> delPageData(
            PageDto pageDto
    ){
        projectPageS3Service.delPage(pageDto.getPageId());
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }
}
