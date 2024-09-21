package org.example.backend.ErrorArchive.Repository;

import org.example.backend.ErrorArchive.Model.Entity.ErrorArchive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ErrorArchiveReository extends JpaRepository<ErrorArchive, Long> {
    // ErrorArchive 엔터티를 데이터베이스에서 categoryName
    // 어떤 엔터티를 쓸지, id의 타입

    Page<ErrorArchive> findAllByCategoryId(Long categoryId, Pageable pageable);

    Page<ErrorArchive> findAllByTitleIsContainingIgnoreCase(String keyword, Pageable pageable);

    Page<ErrorArchive> findAllByContentIsContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT er FROM ErrorArchive  er WHERE LOWER(er.title) LIKE CONCAT('%', :keyword,'%') " +
            "OR LOWER(er.content) LIKE CONCAT('%', :keyword,'%')")
    Page<ErrorArchive> findAllByKeyword(String keyword, Pageable pageable); // 메서드명 줄이기 위해 query 사용

    Page<ErrorArchive> findAllByTitleIsContainingIgnoreCaseAndCategoryId(String keyword, Long categoryId, Pageable pageable);

    Page<ErrorArchive> findAllByContentIsContainingIgnoreCaseAndCategoryId(String keyword, Long categoryId, Pageable pageable);

    @Query("SELECT er FROM ErrorArchive er WHERE (LOWER(er.title) LIKE CONCAT('%', :keyword,'%') " +
            "OR LOWER(er.content) LIKE CONCAT('%', :keyword,'%')) AND er.category.id = :categoryId")
    Page<ErrorArchive> findAllByKeywordAndCategory(String keyword, Long categoryId, Pageable pageable); // 메서드명 줄이기 위해 query 사용
}
