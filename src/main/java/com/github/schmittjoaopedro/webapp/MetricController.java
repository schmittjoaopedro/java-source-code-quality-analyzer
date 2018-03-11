package com.github.schmittjoaopedro.webapp;

import com.github.schmittjoaopedro.analyser.SourceCodeAnalyser;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.SourceCode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MetricController {

    @Resource
    private SourceCodeAnalyser sourceCodeAnalyser;

    @RequestMapping(value = "/metrics", method = RequestMethod.POST)
    public Metric getMetrics(@RequestBody SourceCode sourceCode) {
        return sourceCodeAnalyser.analyse(sourceCode.getSourceCode());
    }

}
