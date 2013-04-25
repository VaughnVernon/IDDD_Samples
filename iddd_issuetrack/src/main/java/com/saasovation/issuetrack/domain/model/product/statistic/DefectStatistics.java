package com.saasovation.issuetrack.domain.model.product.statistic;

public class DefectStatistics {

    public static final DefectStatistics NO_DEFECTIVE_STATISTICS = new NullDefectStatistics();

    public boolean exists() {
	return true;
    }

    private static final class NullDefectStatistics extends DefectStatistics {
	@Override
	public boolean exists() {
	    return false;
	}
    }

}
