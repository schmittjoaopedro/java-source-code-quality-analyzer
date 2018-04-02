package com.github.schmittjoaopedro.webapp;

import com.github.schmittjoaopedro.dto.MetricHeader;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.SourceCode;
import com.github.schmittjoaopedro.service.MetricService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class MetricController {

	@Resource
	private MetricService metricService;
	
    @RequestMapping(value = "/metrics", method = RequestMethod.POST)
    public Metric getMetrics(@RequestBody SourceCode sourceCode) {
        return metricService.calculateMetric(sourceCode);
    }
    
    @RequestMapping(value = "/etl", method = RequestMethod.GET)
    public void startIndex() {
    	metricService.createIndex();
    }

    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public @ResponseBody  List<MetricHeader> findRange(@RequestParam("page") Long page, @RequestParam("limit") Long limit) {
        return metricService.getMetricRange(page, limit);
    }

    @RequestMapping(value = "/metrics/{id}", method = RequestMethod.GET)
    public @ResponseBody  Metric findRange(@PathVariable("id") String id) {
        return metricService.getMetric(id);
    }

}
