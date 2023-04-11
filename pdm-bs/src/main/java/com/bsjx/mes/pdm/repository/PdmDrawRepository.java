package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmDraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdmDrawRepository extends JpaRepository<PdmDraw, String> {

    @Query("select t from PdmDraw t where t.itemId = ?1 and t.itemRev=?2 and fileUrl=?3 and (dataGroup is null or dataGroup =?4)")
    List<PdmDraw> findByFile(String itemId, String itemRev, String fileUrl, String dataGroup);
}
