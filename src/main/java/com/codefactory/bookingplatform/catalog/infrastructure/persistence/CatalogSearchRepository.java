package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * HU-005. Consulta nativa: solo servicios activos de proveedores activos,
 * con filtros combinables. La collation de catalog_service.name y
 * providers.legal_name (utf8mb4_0900_ai_ci, definida en la migracion) hace
 * que LIKE ya sea insensible a mayusculas y tildes sin logica adicional.
 */
@Repository
public class CatalogSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<CatalogSearchRow> search(String keyword, String category, String city, BigDecimal minPrice, BigDecimal maxPrice) {
        StringBuilder sql = new StringBuilder("""
                select s.id, s.name, p.legal_name, s.duration_minutes, s.price, l.city
                from catalog_service s
                join providers p on p.id = s.provider_id
                join service_location sl on sl.service_id = s.id
                join locations l on l.id = sl.location_id
                where s.status = 'ACTIVE' and p.status = 'ACTIVE'
                """);
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" and (s.name like ? or s.description like ? or p.legal_name like ?) ");
            String likeValue = "%" + keyword + "%";
            params.add(likeValue);
            params.add(likeValue);
            params.add(likeValue);
        }
        if (category != null && !category.isBlank()) {
            sql.append(" and p.category = ? ");
            params.add(category);
        }
        if (city != null && !city.isBlank()) {
            sql.append(" and l.city = ? ");
            params.add(city);
        }
        if (minPrice != null) {
            sql.append(" and s.price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" and s.price <= ? ");
            params.add(maxPrice);
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        List<Object[]> rows = query.getResultList();
        List<CatalogSearchRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new CatalogSearchRow(
                    java.util.UUID.fromString(String.valueOf(row[0])),
                    (String) row[1],
                    (String) row[2],
                    ((Number) row[3]).intValue(),
                    (BigDecimal) row[4],
                    (String) row[5]));
        }
        return result;
    }
}
