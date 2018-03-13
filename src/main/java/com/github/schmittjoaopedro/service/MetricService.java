package com.github.schmittjoaopedro.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.schmittjoaopedro.analyser.SourceCodeAnalyser;
import com.github.schmittjoaopedro.database.MetricRepository;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.SourceCode;

@Service
public class MetricService {
	
	@Resource
	private MetricRepository metricRepository;

    @Resource
    private SourceCodeAnalyser sourceCodeAnalyser;
    
	public Metric calculateMetric(SourceCode sourceCode) {
		return sourceCodeAnalyser.analyse(sourceCode);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Metric calculateMetricAndSave(SourceCode sourceCode) {
		return metricRepository.save(sourceCodeAnalyser.analyse(sourceCode));
	}

}
