package com.allweb.rms.repository.elastic;

import com.allweb.rms.entity.elastic.InterviewElasticsearchDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface InterviewElasticsearchRepository
    extends ElasticsearchRepository<InterviewElasticsearchDocument, Integer>,
        InterviewCustomElasticsearchRepository {

  List<InterviewElasticsearchDocument> findByCandidateId(int candidateId);
}
