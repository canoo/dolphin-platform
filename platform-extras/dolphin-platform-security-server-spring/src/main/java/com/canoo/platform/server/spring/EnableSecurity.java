package com.canoo.platform.server.spring;

import com.canoo.dp.impl.server.security.spring.SecurityBeanFactory;
import org.apiguardian.api.API;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
@Import({SecurityBeanFactory.class})
@Documented
@Target({TYPE})
@Retention(RUNTIME)
public @interface EnableSecurity {
}
