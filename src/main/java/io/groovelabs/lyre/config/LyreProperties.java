package io.groovelabs.lyre.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = LyreConfiguration.LYRE_PROPERTIES_PREFIX)
public class LyreProperties {

    private int port = 9000;

    private String contextPath = "";

    public ScannerProperties scanner;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public ScannerProperties getScanner() {
        return scanner;
    }

    public void setScanner(ScannerProperties scanner) {
        this.scanner = scanner;
    }
}