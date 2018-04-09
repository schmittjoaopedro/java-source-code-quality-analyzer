package com.github.schmittjoaopedro.analyser.checkstyle;

import java.util.ArrayList;
import java.util.List;

import com.github.schmittjoaopedro.model.CheckstyleMetric;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

public class CheckstyleListener implements AuditListener {

    private List<CheckstyleMetric> checkstyleMetrics;

    @Override
    public void auditStarted(AuditEvent auditEvent) { }

    @Override
    public void auditFinished(AuditEvent auditEvent) { }

    @Override
    public void fileStarted(AuditEvent auditEvent) { }

    @Override
    public void fileFinished(AuditEvent auditEvent) { }

    @Override
    public void addError(AuditEvent auditEvent) {
        CheckstyleMetric checkstyleMetric = new CheckstyleMetric();
        checkstyleMetric.setLine(auditEvent.getLocalizedMessage().getLineNo());
        checkstyleMetric.setDescription(auditEvent.getLocalizedMessage().getMessage());
        checkstyleMetric.setSeverityLevel(auditEvent.getLocalizedMessage().getSeverityLevel().ordinal());
        checkstyleMetric.setName(auditEvent.getLocalizedMessage().getSourceName());
        getCheckstyleMetrics().add(checkstyleMetric);
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable throwable) { }

    public List<CheckstyleMetric> getCheckstyleMetrics() {
        if(checkstyleMetrics == null) checkstyleMetrics = new ArrayList<>();
        return checkstyleMetrics;
    }

    public void setCheckstyleMetrics(List<CheckstyleMetric> checkstyleMetrics) {
        this.checkstyleMetrics = checkstyleMetrics;
    }
}
