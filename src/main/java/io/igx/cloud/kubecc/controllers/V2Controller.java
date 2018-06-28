package io.igx.cloud.kubecc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/v2")
public abstract class V2Controller {
    protected Logger logger = LoggerFactory.getLogger(getClass());
}
