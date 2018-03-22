package com.github.schmittjoaopedro.service;

import java.util.List;

import javax.annotation.Resource;

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
	
	@Resource
	private MetricRepository metricRepository;

    @Resource
    private SourceCodeAnalyser sourceCodeAnalyser;
    
    @Resource
    private OracleETL oracleETL;
    
	public Metric calculateMetric(SourceCode sourceCode) {
		return sourceCodeAnalyser.analyse(sourceCode);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Metric calculateMetricAndSave(SourceCode sourceCode) {
		return metricRepository.save(sourceCodeAnalyser.analyse(sourceCode));
	}
	
	public void createIndex() {
		List<Long> actionIds = oracleETL.getRuleActionsId();
		if(actionIds != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(Long id : actionIds) {
						SourceCode sourceCode = oracleETL.getSourceCode(id);
						sourceCode.setPmd(true);
						sourceCode.setCheckStyle(true);
						sourceCode.setSpotBugs(true);
						Metric metric = sourceCodeAnalyser.analyse(sourceCode);
						metric.setRuleId(id);
						metric.setSourceCode(sourceCode.getSourceCode());
						metric.setUser(sourceCode.getUser());
						metricRepository.save(metric);
					}
				}
			}).start();
		}
	}

}
