package com.org.server.whiteBoardAndPage.repository;


import com.org.server.whiteBoardAndPage.domain.WhiteBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WhiteBoardRepo extends JpaRepository<WhiteBoard,Long>{

}
