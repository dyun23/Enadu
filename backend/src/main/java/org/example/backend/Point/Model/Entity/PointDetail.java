package org.example.backend.Point.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.User.Model.Entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class PointDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private boolean state;
    @Column(nullable = false)
    private Integer point;
    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

}
