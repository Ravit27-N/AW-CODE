package com.allweb.rms.repository.elastic;
import java.util.Map;
import com.allweb.rms.entity.elastic.DemandElasticsearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(exported = false)
public interface DemandElasticRepository extends ElasticsearchRepository<DemandElasticsearchDocument, Integer> ,DemandCustomElasticsearchRepository{

//  @Query("{\"id\": {\"values\": ?0 }}")
//  Map<String, Object> findDemandByIdWithRawQuery(int id);

//  @Query(query = "", count = false)
//  ArrayList<String> find();

//  @Query(
//      query =
//          "select count(d) from demand d where d.project_id =:project_id and d.job_description_id=:description_id")
//  int checkValidateCreateDemand(
//      @Param("project_id") int project_id, @Param("description_id") int description_id);

    int countDemandByProjectIdAndJobDescriptionId(int projectId, int jobDescriptionId);

    int countDemandByProjectIdAndJobDescriptionIdAndId(int projectId, int jobDescriptionId, int demandId);

    Map<String, Object> findDemandById(int id);

}
