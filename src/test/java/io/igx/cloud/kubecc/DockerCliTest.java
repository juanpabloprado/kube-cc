package io.igx.cloud.kubecc;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class DockerCliTest {

    @Test
    public void testBuild() throws Exception {
        Process build = new ProcessBuilder("docker", "build", "-t", "source-demo", ".").directory(new File("/Users/vinicius/tmp/staging/57f34674-67f1-4ba3-8970-0120993bb545/")).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(build.getInputStream()))) {
            String line = null;
            while((line = reader.readLine()) != null){
                System.out.println(line);
            }
        } catch (Exception e) {
        }
    }
}
