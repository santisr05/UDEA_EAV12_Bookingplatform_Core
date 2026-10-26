package com.codefactory.bookingplatform.catalog.application;

import com.codefactory.bookingplatform.catalog.domain.port.NextAvailableSlotQuery;
import com.codefactory.bookingplatform.catalog.infrastructure.persistence.CatalogSearchRepository;
import com.codefactory.bookingplatform.catalog.infrastructure.persistence.CatalogSearchRow;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * HU-005. La busqueda insensible a mayusculas/tildes se apoya en la
 * collation utf8mb4_0900_ai_ci (accent/case insensitive) definida en la
 * migracion de la tabla catalog_service, en vez de una funcion UNACCENT
 * (que MySQL no tiene de forma nativa, a diferencia de PostgreSQL).
 */
@Service
public class SearchCatalogUseCase {

    private final CatalogSearchRepository catalogSearchRepository;
    private final NextAvailableSlotQuery nextAvailableSlotQuery;

    public SearchCatalogUseCase(CatalogSearchRepository catalogSearchRepository,
                                 NextAvailableSlotQuery nextAvailableSlotQuery) {
        this.catalogSearchRepository = catalogSearchRepository;
        this.nextAvailableSlotQuery = nextAvailableSlotQuery;
    }

    public record CatalogItem(CatalogSearchRow service, Instant nextAvailableSlot) {}

    public List<CatalogItem> search(String keyword, String category, String city, BigDecimal minPrice, BigDecimal maxPrice) {
        List<CatalogSearchRow> rows = catalogSearchRepository.search(keyword, category, city, minPrice, maxPrice);
        return rows.stream()
                .map(row -> new CatalogItem(row, nextAvailableSlotQuery.nextAvailableSlot(row.serviceId()).orElse(null)))
                .toList();
    }
}
