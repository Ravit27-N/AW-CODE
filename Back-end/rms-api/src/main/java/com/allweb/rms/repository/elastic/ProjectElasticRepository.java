package com.allweb.rms.repository.elastic;

import com.allweb.rms.entity.elastic.Project;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectElasticRepository extends ElasticsearchRepository<Project, Integer> {}
