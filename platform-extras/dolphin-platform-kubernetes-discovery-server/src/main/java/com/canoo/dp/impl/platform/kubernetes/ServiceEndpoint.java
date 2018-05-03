package com.canoo.dp.impl.platform.kubernetes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/service")
public class ServiceEndpoint {

    @Autowired
    private ServiceHandler serviceHandler;

    @RequestMapping(value = "/{namespace}/{name}", method = GET)
    public String getAddress(@PathVariable("namespace") final String namespace, @PathVariable("name") final String name) {
        return serviceHandler.getAddress(namespace, name);
    }

    @RequestMapping(value = "/{name}", method = GET)
    public String getAddress(@PathVariable("name") final String name) {
        return serviceHandler.getAddress(name);
    }

}
