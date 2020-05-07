package com.github.schmittjoaopedro.database;

import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.SourceCode;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class SourceCodeLoader {

    public List<Metric> getMetrics() {
        LinkedList<Metric> metrics = new LinkedList<>();
        try {
            for (File sourceFile : getResourceFiles("samples")) {
                Metric metric = new Metric();
                SourceCode sourceCode = new SourceCode();
                sourceCode.setClassName(sourceFile.getName());
                metric.setName(sourceCode.getClassName());
                sourceCode.setSourceCode(FileUtils.readFileToString(sourceFile, Charset.forName("UTF-8")));
                sourceCode.setDescription(sourceFile.getName());
                sourceCode.setUserCreated(Files.getOwner(Paths.get(sourceFile.getAbsolutePath())).getName());
                sourceCode.setDateCreated(new Date(Files.getLastModifiedTime(Paths.get(sourceFile.getAbsolutePath())).toMillis()));
                metric.setSourceCode(sourceCode);
                metrics.add(metric);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metrics;
    }

    private List<File> getResourceFiles(String path) throws IOException {
        LinkedList<File> files = new LinkedList<>();
        InputStream in = getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String resource;
        File file;
        while ((resource = br.readLine()) != null) {
            file = new File(getClass().getClassLoader().getResource(path + "/" + resource).getFile());
            files.add(file);
        }
        return files;
    }

    private InputStream getResourceAsStream(String resource) {
        InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
