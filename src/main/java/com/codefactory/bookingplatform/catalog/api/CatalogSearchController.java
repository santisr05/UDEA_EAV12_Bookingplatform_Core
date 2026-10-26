package com.codefactory.bookingplatform.catalog.api;

import com.codefactory.bookingplatform.catalog.application.SearchCatalogUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogSearchController {

    private final SearchCatalogUseCase searchCatalogUseCase;

    public CatalogSearchController(SearchCatalogUseCase searchCatalogUseCase) {
        this.searchCatalogUseCase = searchCatalogUseCase;
    }

    @GetMapping("/services")
    public List<SearchCatalogUseCase.CatalogItem> search(@RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) String category,
                                                           @RequestParam(required = false) String city,
                                                           @RequestParam(required = false) BigDecimal minPrice,
                                                           @RequestParam(required = false) BigDecimal maxPrice) {
        return searchCatalogUseCase.search(keyword, category, city, minPrice, maxPrice);
    }
}
