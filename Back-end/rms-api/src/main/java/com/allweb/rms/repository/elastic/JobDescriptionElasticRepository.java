package com.allweb.rms.repository.elastic;

import com.allweb.rms.entity.elastic.JobDescription;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDescriptionElasticRepository extends ElasticsearchRepository<JobDescription, Integer> {}
