package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmBom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PdmBomRepository extends JpaRepository<PdmBom, String> {

}
