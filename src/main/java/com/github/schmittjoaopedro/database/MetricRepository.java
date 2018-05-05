package com.github.schmittjoaopedro.database;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.schmittjoaopedro.model.Metric;

public interface MetricRepository extends MongoRepository<Metric, String> {

    Long countByStatisticsStatisticGreaterThan(double statistic);

    Long countByStatisticsStatisticGreaterThanAndStatisticsComplexityClass(double statistic, int complexityClass);

    Long countByStatisticsComplexityClass(int complexityClass);

}
