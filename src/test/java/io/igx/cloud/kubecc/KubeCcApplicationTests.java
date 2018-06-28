package io.igx.cloud.kubecc;

import io.igx.cloud.kubecc.domain.ApplicationStagedEvent;
import io.igx.cloud.kubecc.services.KubeDeployerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KubeCcApplicationTests {

	@Autowired
	private KubeDeployerService deployerService;

	@Test
	public void contextLoads() {
		deployerService.deployApplication(new ApplicationStagedEvent("c88e990f-95e1-441b-b3d5-200271973da9"));

	}

}
