package com.saasovation.issuetrack.domain.model.product.statistic;

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.transform;
import static com.saasovation.issuetrack.domain.model.product.statistic.DefectStatistics.NO_DEFECTIVE_STATISTICS;

import org.apache.commons.lang.NotImplementedException;

import com.google.common.base.Function;
import com.saasovation.issuetrack.domain.model.product.Product;
import com.saasovation.issuetrack.domain.model.product.ProductRepository;
import com.saasovation.issuetrack.domain.model.tenant.TenantId;

public class DefectStatisticsCalculator {

    private ProductRepository repository;

    public DefectStatistics mostDefectiveProduct(TenantId aTenantId) {
	return getFirst(allDefectStatisticsFor(aTenantId), NO_DEFECTIVE_STATISTICS);
    }

    public Iterable<DefectStatistics> allDefectStatisticsFor(TenantId aTenantId) {
	return transform(allProductsOf(aTenantId), calculateDefectStatistics());
    }

    private Iterable<Product> allProductsOf(TenantId aTenantId) {
	return repository.getAll(aTenantId);
    }

    private Function<Product, DefectStatistics> calculateDefectStatistics() {
	return new Function<Product, DefectStatistics> () {
	    @Override
	    public DefectStatistics apply(Product product) {
		throw new NotImplementedException();
	    }
	};
    }
}
