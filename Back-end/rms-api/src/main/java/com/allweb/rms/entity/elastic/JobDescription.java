package com.allweb.rms.entity.elastic;

import com.allweb.rms.entity.jpa.AbstractEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = JobDescription.INDEX)
@NoArgsConstructor
@Data
public class JobDescription extends AbstractEntity implements Serializable {

    public static final String INDEX = "idx_job_description";

    @Id
    @Field(type = FieldType.Integer)
    private int id;

    @Field(name = "title", type = FieldType.Text, normalizer = "lowercase")
    private String title;

    @Field(name = "description", type = FieldType.Text, normalizer = "lowercase")
    private String description;

    @Field(name = "file_name", type = FieldType.Text, normalizer = "lowercase")
    private String filename;

    @Field(name = "active", type = FieldType.Boolean, normalizer = "lowercase")
    private boolean active;
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof com.allweb.rms.entity.jpa.Demand demand)) return false;
        return Objects.equals(id, demand.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @PrePersist
    void onCreate() {
        setCreatedAt(new Date());
        setUpdatedAt(new Date());
    }

    @PreUpdate
    void onUpdate() {
        setUpdatedAt(new Date());
    }

}