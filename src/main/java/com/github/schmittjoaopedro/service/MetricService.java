package com.github.schmittjoaopedro.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.github.schmittjoaopedro.analyser.MetricCalculator;
import com.github.schmittjoaopedro.dto.MetricHeader;
import org.apache.logging.log4j.LogManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.schmittjoaopedro.analyser.SourceCodeAnalyser;
import com.github.schmittjoaopedro.database.MetricRepository;
import com.github.schmittjoaopedro.database.OracleETL;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.SourceCode;

@Service
public class MetricService {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(MetricService.class);

    @Resource
    private MetricRepository metricRepository;

    @Resource
    private SourceCodeAnalyser sourceCodeAnalyser;

    @Resource
    private OracleETL oracleETL;

    public Metric calculateMetric(SourceCode sourceCode) {
        Metric metric = sourceCodeAnalyser.analyse(sourceCode);
        long total = metricRepository.count();
        long lessThan = metricRepository.countByComplexityFactorGreaterThan(metric.getComplexityFactor());
        metric.setPercentage((double) lessThan / total);
        return metric;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Metric calculateMetricAndSave(SourceCode sourceCode) {
        return metricRepository.save(sourceCodeAnalyser.analyse(sourceCode));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createIndex() {
        List<Long> actionIds = oracleETL.getRuleActionsId();
        if (actionIds != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    for (Long id : actionIds) {
                        try {
                            Metric metric = oracleETL.getMetric(id);
                            SourceCode sourceCode = new SourceCode();
                            sourceCode.setPmd(true);
                            sourceCode.setCheckStyle(true);
                            sourceCode.setSpotBugs(true);
                            sourceCode.setSourceCode(metric.getSourceCode());
                            sourceCodeAnalyser.analyse(sourceCode, metric);
                            metricRepository.save(metric);
                        } catch (Exception ex) {
                            logger.error(ex);
                            logger.error("Error on load " + id);
                        }
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
                            item.getRuleVersionId(),
                            item.getUserCreated(),
                            item.getUserUpdated(),
                            item.getDateCreated(),
                            item.getDateUpdated(),
                            item.getClassComplexity()))
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Metric getMetric(String id) {
        return metricRepository.findById(id).get();
    }

}
