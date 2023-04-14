package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository

public interface PdmOptionRepository extends JpaRepository<PdmOption, String> {
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "delete from PdmOption po where po.processId=?1 and po.dataGroup=?2")
    int deleteByProcessId(String processId, String dataGroup);
}
