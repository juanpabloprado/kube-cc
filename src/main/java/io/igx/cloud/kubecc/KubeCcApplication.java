package io.igx.cloud.kubecc;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class KubeCcApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubeCcApplication.class, args);
	}

	@Bean
	public KubernetesClient kubernetesClient(){
		return new DefaultKubernetesClient();
	}


	@Bean
	ApplicationEventMulticaster applicationEventMulticaster() {
		SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
		eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
		eventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER);
		return eventMulticaster;
	}
}
