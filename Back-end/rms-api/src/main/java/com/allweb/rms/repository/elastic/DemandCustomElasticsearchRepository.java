package com.allweb.rms.repository.elastic;

import com.allweb.rms.entity.dto.DemandElasticsearchRequest;
import com.allweb.rms.entity.dto.DemandResponse;
import com.allweb.rms.entity.elastic.DemandElasticsearchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DemandCustomElasticsearchRepository {

    Page<DemandElasticsearchDocument> findAllByElasticsearch(
            DemandElasticsearchRequest request);
}

