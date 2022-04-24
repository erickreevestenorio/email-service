package com.exercise.email.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class BaseIdEntity<T> extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected T id;

    @Builder(builderMethodName = "baseIdEntityBuilder")
    public BaseIdEntity(String correlationId, long version, Date createdOn, Date updatedOn, String createdBy, String lastModifiedBy, T id) {
        super(correlationId, version, createdOn, updatedOn, createdBy, lastModifiedBy);
        this.id = id;
    }

}