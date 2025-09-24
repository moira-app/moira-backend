package com.org.server.certification.controller;


import com.org.server.certification.service.CertificationService;
import com.org.server.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cert")
public class CertificationController {

    private final CertificationService certificationService;
    @GetMapping("/create/{mail}")
    public ResponseEntity<ApiResponseUtil<String>> createCode(@PathVariable(name ="mail")String mail){
        certificationService.createCertCode(mail);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }

    @GetMapping("/check/{mail}/{code}")
    public ResponseEntity<ApiResponseUtil<String>> checkCode(@PathVariable(name = "mail")String mail,
                                                       @PathVariable(name="code")String code){
        certificationService.checkCode(mail,code);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }
}
