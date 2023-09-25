package com.tessi.cxm.pfl.ms5.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "PASSWORD_ARCHIVE_SEQUENCE_GENERATOR",
        sequenceName = "PASSWORD_ARCHIVE_SEQUENCE",
        allocationSize = 1)
public class PasswordArchive extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PASSWORD_ARCHIVE_SEQUENCE_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "CHANGE_DATE")
    private LocalDateTime changeDate;

    @Column(name = "PASSWORD")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PasswordArchive)) {
            return false;
        }
        PasswordArchive that = (PasswordArchive) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getUserEntity().getId(), that.getUserEntity().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getUserEntity().getId());
    }


    @PrePersist
    private void create() {
        setCreatedAt(new Date());
    }

    @PreUpdate
    private void update() {
        setLastModified(new Date());
    }
}
