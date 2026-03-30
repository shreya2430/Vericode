package com.vericode.checker;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CheckstyleAdapter implements CodeChecker {

    private static final String CONFIG_PATH = "/checkstyle-config.xml";

    @Override
    public CheckResult check(String code) {
        CheckResult result = new CheckResult();

        if (code == null || code.isBlank()) {
            result.addViolation(new Violation("LINT", "Code is empty", 0, "ERROR"));
            return result;
        }

        File tempFile = null;
        try {
            tempFile = writeTempFile(code);
            List<Violation> violations = runCheckstyle(tempFile);
            result.addAll(violations);
        } catch (Exception e) {
            result.addViolation(new Violation("SYSTEM",
                    "Checkstyle analysis failed: " + e.getMessage(), 0, "WARNING"));
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        return result;
    }

    private File writeTempFile(String code) throws Exception {
        File tempFile = File.createTempFile("vericode_", ".java");
        Files.writeString(tempFile.toPath(), code);
        return tempFile;
    }

    private List<Violation> runCheckstyle(File file) throws CheckstyleException {
        List<Violation> violations = new ArrayList<>();

        InputStream configStream = getClass().getResourceAsStream(CONFIG_PATH);
        if (configStream == null) {
            violations.add(new Violation("SYSTEM",
                    "Checkstyle config not found at " + CONFIG_PATH, 0, "WARNING"));
            return violations;
        }

        Configuration config = ConfigurationLoader.loadConfiguration(
                new org.xml.sax.InputSource(configStream),
                new PropertiesExpander(new Properties()),
                ConfigurationLoader.IgnoredModulesOptions.OMIT);

        Checker checker = new Checker();
        checker.setModuleClassLoader(getClass().getClassLoader());
        checker.configure(config);

        ViolationCollector collector = new ViolationCollector(violations);
        checker.addListener(collector);

        checker.process(List.of(file));
        checker.destroy();

        return violations;
    }

    /**
     * AuditListener that collects Checkstyle events into our Violation format.
     */
    private static class ViolationCollector implements AuditListener {

        private final List<Violation> violations;

        ViolationCollector(List<Violation> violations) {
            this.violations = violations;
        }

        @Override
        public void auditStarted(AuditEvent event) {}

        @Override
        public void auditFinished(AuditEvent event) {}

        @Override
        public void fileStarted(AuditEvent event) {}

        @Override
        public void fileFinished(AuditEvent event) {}

        @Override
        public void addError(AuditEvent event) {
            String severity = mapSeverity(event.getSeverityLevel());
            String type = "CHECKSTYLE";
            String message = event.getMessage();
            int line = event.getLine();

            violations.add(new Violation(type, message, line, severity));
        }

        @Override
        public void addException(AuditEvent event, Throwable throwable) {
            violations.add(new Violation("SYSTEM",
                    "Checkstyle internal error: " + throwable.getMessage(), 0, "WARNING"));
        }

        private String mapSeverity(SeverityLevel level) {
            return switch (level) {
                case ERROR -> "ERROR";
                case WARNING -> "WARNING";
                case INFO -> "INFO";
                default -> "WARNING";
            };
        }
    }
}
