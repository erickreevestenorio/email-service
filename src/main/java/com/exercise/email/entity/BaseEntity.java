package com.exercise.email.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Builder(builderMethodName = "baseEntityBuilder")
    public BaseEntity(String correlationId, long version, Date createdOn, Date updatedOn, String createdBy, String lastModifiedBy) {
        this.correlationId = correlationId;
        this.version = version;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
    }

    @Column(nullable = false)
    private String correlationId;

    @Version
    @Column(nullable = false)
    private long version = 0L;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Date createdOn;

    @Column(nullable = false)
    @LastModifiedDate
    private Date updatedOn;

    @CreatedBy
    @Column(nullable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private String lastModifiedBy;

}
