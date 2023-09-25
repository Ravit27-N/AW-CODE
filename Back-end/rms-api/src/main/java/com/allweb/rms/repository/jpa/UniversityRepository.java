package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.University;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@SecurityRequirement(name = "bearer")
public interface UniversityRepository extends JpaRepository<University, Integer> {

  // https://www.postgresql.org/docs/current/textsearch-controls.html
  @Query(
      value =
          "select u.id, u.created_at, u.created_by, u.last_modified_by, u.updated_at, u.\"name\",u.address,"
              + "ts_rank_cd(to_tsvector('simple',u.\"name\"), query, 16) as score "
              + "from university u, plainto_tsquery(lower(?1)) query "
              + "where lower(u.\"name\") @@ query "
              + "order by score desc",
      nativeQuery = true)
  Page<University> findByRelevantName(String filter, Pageable pageable);

  Page<University> findByNameStartingWithIgnoreCase(String name, Pageable pageable);

  @Query(
      value = "select count(u) from university u where name ilike '%'||?1||'%'",
      nativeQuery = true)
  Long validateUniversity(String name);

  @Query(
      value = "select count(u) from university u where id != ?1 and name ilike '%'||?2||'%'",
      nativeQuery = true)
  Long validateUniversityOnUpdate(int id, String name);
}
