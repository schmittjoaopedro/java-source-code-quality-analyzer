package com.github.schmittjoaopedro.webapp;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.SourceCode;
import com.github.schmittjoaopedro.service.MetricService;

@RestController
public class MetricController {

	@Resource
	private MetricService metricService;
	
    @RequestMapping(value = "/metrics", method = RequestMethod.POST)
    public Metric getMetrics(@RequestBody SourceCode sourceCode) {
        return metricService.calculateMetric(sourceCode);
    }

}
