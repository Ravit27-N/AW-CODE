package com.allweb.rms.repository.elastic;

import com.allweb.rms.entity.elastic.CandidateElasticsearchDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface CandidateElasticsearchRepository
    extends ElasticsearchRepository<CandidateElasticsearchDocument, Integer>,
        CandidateCustomElasticsearchRepository {
  List<CandidateElasticsearchDocument> findByInterviewsId(int interviewId);
}
