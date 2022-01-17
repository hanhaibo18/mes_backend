package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.DrawingApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrawingApplyRepository extends JpaRepository<DrawingApply, String> {
    Page<DrawingApply> findByStatus(String status, Pageable pageable);
    Page<DrawingApply> findByDrawingNoAndStatus(String drawingNo, String status, Pageable pageable);
}
