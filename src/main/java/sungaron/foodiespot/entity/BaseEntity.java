package sungaron.foodiespot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass //추상클래스에 사용할 수 있으며 엔티티가 될 수 없고 상속을 통해서 사용해야 함
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate //jpa 저장소가 save()할 때 자동으로 생성시간을 만듦
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate //jpa 저장소가 수정할 때 자동으로 생성시간을 만듦
    private LocalDateTime lastModifiedAt;
}
