package com.github.schmittjoaopedro.checkstyle;

import com.github.schmittjoaopedro.metrics.CheckstyleMetrics;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CheckstyleListener implements AuditListener {

    private static Logger logger = LogManager.getLogger(CheckstyleListener.class);

    private List<CheckstyleMetrics> checkstyleMetrics;

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
        CheckstyleMetrics checkstyleMetrics = new CheckstyleMetrics();
        checkstyleMetrics.setLine(auditEvent.getLocalizedMessage().getLineNo());
        checkstyleMetrics.setDescription(auditEvent.getLocalizedMessage().getMessage());
        checkstyleMetrics.setSeverityLevel(auditEvent.getLocalizedMessage().getSeverityLevel().ordinal());
        getCheckstyleMetrics().add(checkstyleMetrics);
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable throwable) { }

    public List<CheckstyleMetrics> getCheckstyleMetrics() {
        if(checkstyleMetrics == null) checkstyleMetrics = new ArrayList<>();
        return checkstyleMetrics;
    }

    public void setCheckstyleMetrics(List<CheckstyleMetrics> checkstyleMetrics) {
        this.checkstyleMetrics = checkstyleMetrics;
    }
}
