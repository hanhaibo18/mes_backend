package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PdmObjectRepository extends JpaRepository<PdmObject, String> {
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "delete from PdmObject o where o.opId=?1 and o.dataGroup=?2")
    int deleteByOpId(String opId,String dataGroup);
}
