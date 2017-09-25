package com.github.groovylabs.lyre.engine.scanner;

import com.github.groovylabs.lyre.config.LyreProperties;
import com.github.groovylabs.lyre.engine.reader.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class Scanner implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scanner.class);

    @Autowired
    private Reader reader;

    @Autowired
    private LyreProperties lyreProperties;

    private List<File> files = new ArrayList<>();

    public void afterPropertiesSet() throws Exception {
        scan();
    }

    //@PostConstruct
    public void Scanner() {
        scan();
    }

    public void scan() {

        LOGGER.info("Scanning [*{}] files on path: [{}]",
            lyreProperties.getFileFormat(), lyreProperties.getScanPath());

        File folder = new File(lyreProperties.getScanPath());

        if (folder.isDirectory()) {

            File[] fileList = folder.listFiles();

            searchFiles(fileList);

            if (lyreProperties.isEnableLivereload()) {
                Thread watcher = new Thread(new Watcher(this, files, lyreProperties));
                watcher.start();
            } else {
                LOGGER.info("Watcher disabled, " +
                    "to enable live-reload feature set 'lyre.enable-livereload=true' on properties");
            }

            reader.read(files.toArray(new File[]{}));

        } else {
            LOGGER.error("Error while scanning, scan-path property must point to a valid directory.");
        }

    }

    private void searchFiles(File[] fileList) {

        for (File file : fileList) {

            if (file.isFile() && file.getName().endsWith(lyreProperties.getFileFormat()))
                files.add(file);
            else if (file.isDirectory())
                searchFiles(file.listFiles());

        }
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public LyreProperties getLyreProperties() {
        return lyreProperties;
    }

    public void setLyreProperties(LyreProperties lyreProperties) {
        this.lyreProperties = lyreProperties;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}