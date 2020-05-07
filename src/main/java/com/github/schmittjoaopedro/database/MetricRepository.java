package com.github.schmittjoaopedro.database;

import com.github.schmittjoaopedro.model.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricRepository extends JpaRepository<Metric, String> {

    Long countByStatisticsStatisticGreaterThan(double statistic);

    Long countByStatisticsStatisticGreaterThanAndStatisticsComplexityClass(double statistic, int complexityClass);

    Long countByStatisticsComplexityClass(int complexityClass);

}
