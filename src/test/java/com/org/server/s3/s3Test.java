package com.org.server.s3;

import com.org.server.support.IntegralTestEnv;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class s3Test extends IntegralTestEnv {





    @Test
    void imgCreate(){
        String getpreSignUrl=s3Service.getPreSignUrl("testlocationg");
        String savepreSignUrl=s3Service.savePreSignUrl("png/" +
                "image","testlocationg");
        Assertions.assertThat(getpreSignUrl).isNotBlank();
        Assertions.assertThat(getpreSignUrl).isNotEmpty();

        Assertions.assertThat(savepreSignUrl).isNotBlank();
        Assertions.assertThat(savepreSignUrl).isNotEmpty();
    }



}
