
package com.example.plc.repository;

import com.example.plc.entity.Button;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ButtonRepository extends JpaRepository<Button, Long> {

    @Query("SELECT b FROM Button b WHERE b.schemeId = :schemeId")
    List<Button> findBySchemeId(@Param("schemeId") Long schemeId);
}

