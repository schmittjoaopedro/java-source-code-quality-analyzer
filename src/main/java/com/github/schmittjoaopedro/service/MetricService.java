package com.github.schmittjoaopedro.service;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.github.schmittjoaopedro.analyser.MetricCalculator;
import com.github.schmittjoaopedro.dto.MetricHeader;
import com.github.schmittjoaopedro.model.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.schmittjoaopedro.analyser.SourceCodeAnalyser;
import com.github.schmittjoaopedro.database.MetricRepository;
import com.github.schmittjoaopedro.database.OracleETL;

@Service
public class MetricService {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(MetricService.class);

    @Resource
    private MetricRepository metricRepository;

    @Resource
    private SourceCodeAnalyser sourceCodeAnalyser;

    @Resource
    private OracleETL oracleETL;

    public Metric calculateMetric(Metric metric) {
        try {
            sourceCodeAnalyser.analyse(metric);
            return metric;
        } catch (Exception ex) {
            metric = new Metric();
            metric.setError(ex.getMessage() + "\n" + ex.getCause());
            return metric;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Metric calculateMetricAndSave(SourceCode sourceCode) {
        return metricRepository.save(sourceCodeAnalyser.analyse(sourceCode));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Scheduled(cron = "0 0 22 * * ?", zone = "GMT-03:00")
    public void createIndex() {
        metricRepository.deleteAll();
        List<Long> actionIds = oracleETL.getRuleActionsId();
        if (actionIds != null) {
            new Thread(() -> {
                for (Long id : actionIds) {
                    try {
                        Metric metric = oracleETL.getMetric(id);
                        metric.setPmd(true);
                        metric.setCheckStyle(true);
                        metric.setCyclomaticComplexity(true);
                        sourceCodeAnalyser.analyse(metric);
                        metricRepository.save(metric);
                    } catch (Exception ex) {
                        logger.error(ex);
                        logger.error("Error on load " + id);
                    }
                }
            }).start();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<MetricHeader> getMetricRange(Long page, Long limit) {
        return metricRepository
                .findAll(PageRequest.of(page.intValue(), limit.intValue()))
                .getContent()
                .stream()
                .map(item ->
                    new MetricHeader(
                            item.getId(),
                            item.getSourceCode().getRuleVersionId(),
                            item.getSourceCode().getUserCreated(),
                            item.getSourceCode().getUserUpdated(),
                            item.getSourceCode().getDateCreated(),
                            item.getSourceCode().getDateUpdated(),
                            item.getStatistics().getStatistic()))
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Metric getMetric(String id) {
        return metricRepository.findById(id).get();
    }

    @Transactional
    public Metric getStatistic(Metric metric) {
        metric = calculateMetric(metric);
        if(metric.getError() == null) {
            double position = metricRepository.countByStatisticsStatisticGreaterThanAndStatisticsComplexityClass(metric.getStatistics().getStatistic(), metric.getStatistics().getComplexityClass());
            double total = metricRepository.countByStatisticsComplexityClass(metric.getStatistics().getComplexityClass());
            metric.getStatistics().setPosition(position / total);
            metric.setPmdMetrics(metric.getPmdMetrics().stream().sorted(Comparator.comparing(PMDMetric::getPriority).reversed()).collect(Collectors.toList()));
            metric.setCyclomaticComplexities(metric.getCyclomaticComplexities().stream().sorted(Comparator.comparing(CyclomaticComplexity::getCyclomatic).reversed()).collect(Collectors.toList()));
            metric.setCheckstyleMetrics(metric.getCheckstyleMetrics().stream().sorted(Comparator.comparing(CheckstyleMetric::getSeverityLevel).reversed()).collect(Collectors.toList()));
        }
        return metric;
    }

}
