package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmBom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdmBomRepository extends JpaRepository<PdmBom, String> {

}
