package com.trophix.api.offers.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

interface OfferSpringDataRepository extends JpaRepository<OfferEntity, UUID> {

    @Query("""
            select o from OfferEntity o
            where o.isActive = true and (:category is null or o.category = :category)
            order by o.createdAt desc""")
    Page<OfferEntity> findPublicOffers(@Param("category") String category, Pageable pageable);

    @Query("""
            select o from OfferEntity o
            where (:category is null or o.category = :category)
            order by o.createdAt desc""")
    Page<OfferEntity> findAllFiltered(@Param("category") String category, Pageable pageable);
}
