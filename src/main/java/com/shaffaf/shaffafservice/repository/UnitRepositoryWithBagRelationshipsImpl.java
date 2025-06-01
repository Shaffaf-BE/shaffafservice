package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.Unit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class UnitRepositoryWithBagRelationshipsImpl implements UnitRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String UNITS_PARAMETER = "units";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Unit> fetchBagRelationships(Optional<Unit> unit) {
        return unit.map(this::fetchFeesCollections);
    }

    @Override
    public Page<Unit> fetchBagRelationships(Page<Unit> units) {
        return new PageImpl<>(fetchBagRelationships(units.getContent()), units.getPageable(), units.getTotalElements());
    }

    @Override
    public List<Unit> fetchBagRelationships(List<Unit> units) {
        return Optional.of(units).map(this::fetchFeesCollections).orElse(Collections.emptyList());
    }

    Unit fetchFeesCollections(Unit result) {
        return entityManager
            .createQuery("select unit from Unit unit left join fetch unit.feesCollections where unit.id = :id", Unit.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Unit> fetchFeesCollections(List<Unit> units) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, units.size()).forEach(index -> order.put(units.get(index).getId(), index));
        List<Unit> result = entityManager
            .createQuery("select unit from Unit unit left join fetch unit.feesCollections where unit in :units", Unit.class)
            .setParameter(UNITS_PARAMETER, units)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
