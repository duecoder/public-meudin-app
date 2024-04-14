package br.com.due.meudin.repository;

import br.com.due.meudin.domain.info.SpendCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpendCategoryRepository extends JpaRepository<SpendCategory, Long> {
    List<SpendCategory> findByUserId(Long userId);

    @Query(value = "SELECT * FROM info.spend_category WHERE user_id = ?1 " +
            "AND user_default IS TRUE;", nativeQuery = true)
    SpendCategory findUserDefaultByUserId(Long userId);
}
