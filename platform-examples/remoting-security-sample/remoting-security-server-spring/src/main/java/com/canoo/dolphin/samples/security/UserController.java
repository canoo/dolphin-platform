package com.canoo.dolphin.samples.security;

import com.canoo.platform.remoting.server.RemotingController;
import com.canoo.platform.remoting.server.RemotingModel;
import com.canoo.platform.server.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static com.canoo.dolphin.samples.security.Constants.USER_CONTROLLER;

@RemotingController(USER_CONTROLLER)
public class UserController {

    @RemotingModel
    private UserBean model;

    @Autowired
    private SecurityContext securityContext;

    @PostConstruct
    public void init() {
        model.setUserName(securityContext.getUser().getUserName());
        model.setMailAddress(securityContext.getUser().getEmail());

        model.userNameProperty().onChanged(evt -> {
            System.out.println("cahnged");
        });

        model.mailAddressProperty().onChanged(evt -> {
            System.out.println("cahnged");
        });
    }
}
