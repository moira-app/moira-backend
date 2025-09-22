package com.org.server.whiteBoardAndPage.repository;
import com.org.server.whiteBoardAndPage.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PageRepo extends JpaRepository<Page,Long>{
    List<Page> findByWhiteBoardId(Long whiteBoardId);
}
