<p align="right">
<a href="http://www.canoo.com"><img src="http://www.guigarage.com/wordpress/wp-content/uploads/2016/08/canoo_support.png"/></a>
</p>

# Dolphin Platform [![Travis Build](https://travis-ci.org/canoo/dolphin-platform.svg?branch=master)](https://travis-ci.org/canoo/dolphin-platform) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.canoo.dolphin-platform/dolphin-platform-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.canoo.dolphin-platform/dolphin-platform-core) [![Slack chat](https://join-dolphin-platform-slack.herokuapp.com/badge.svg)](https://join-dolphin-platform-slack.herokuapp.com/)
                                                                                                                               

This repository contains all Java related sources of the Dolphin Platform. Clients for 
other languages can be found in seperate repositories 
([JavaScript](https://github.com/canoo/dolphin-platform-js), [AngularJS](https://github.com/canoo/dolphin-platform-angularjs), 
[Polymer](https://github.com/canoo/dolphin-platform-polymer) and [Android](https://github.com/canoo/dolphin-platform-android)).

If you have any questions or comments about Dolphin Platform you can [join our community chat at Slack](https://join-dolphin-platform-slack.herokuapp.com/).

![Dolphin Platform Logo](doc/logo.png)

The Dolphin Platform is a framework that implements the presentation model pattern and provides a modern way to create enterprise applications. The Platform provides several client implementations that all can be used in combination with a general sever API.

By doing so you can create enterprise application with a single server and several desktop, web and mobile client implementations. Here the Dolphin Platforms define a mechanism to automatically snchronize models between the server and the client.

For more information read the [documentation](https://canoo.github.io/dolphin-platform/) of the Dolphin Platform.

## How to use it
You can simply integrate Dolphin Platform in a Spring based application. To do so you only need to add our Spring plugin:
```xml
<dependency>
    <groupId>com.canoo.dolphin-platform</groupId>
    <artifactId>dolphin-platform-server-spring</artifactId>
    <version>0.14.0</version>
</dependency>
```

Next to Spring we provide support for JavaEE. To do so you only need to add our JavaEE plugin:
```xml
<dependency>
    <groupId>com.canoo.dolphin-platform</groupId>
    <artifactId>dolphin-platform-server-javaee</artifactId>
    <version>0.14.0</version>
</dependency>
```


For a JavaFX based client you need to add the following dependency:
```xml
<dependency>
    <groupId>com.canoo.dolphin-platform</groupId>
    <artifactId>dolphin-platform-client-javafx</artifactId>
    <version>0.14.0</version>
</dependency>
```

Next to JavaFX you can use Dolphin Platform in any web client. You can find additonal information in the readme of the client libraries: ([JavaScript](https://github.com/canoo/dolphin-platform-js), [Polymer](https://github.com/canoo/dolphin-platform-polymer) or [Android](https://github.com/canoo/dolphin-platform-android))

In addition you can use [our Maven archetype](http://www.guigarage.com/2015/12/dolphin-platform-jumpstart/) to create a complete server-client-project based on Dolphin Platform.
If you want to create your new project from command line by using an archetype you can simply call this Maven command and select one of the shown Dolphin Platform archetypes:
```shell
mvn archetype:generate -Dfilter=com.canoo.dolphin-platform:
```
Currently the projects contains only a JavaFX based client but it’s planned to add a Polymer based client to the archetypes with the next release.

A complete "Getting started" documentation can be found [here](https://canoo.github.io/dolphin-platform/#_dolphin_platform_jumpstart).

## Useful links
* [Documentation](https://canoo.github.io/dolphin-platform/)
* [Dolphin Platform @ Twitter](https://twitter.com/DolphinPlatform)
* [StackOverflow](http://stackoverflow.com/questions/tagged/dolphin-platform)
* [Dolphin Platform community chat @ SLACK](https://join-dolphin-platform-slack.herokuapp.com/)

The following GitHub repositories provides additional sources for the Dolphin Platform 
* [Android client API Repository](https://github.com/canoo/dolphin-platform-android)
* [JavaScript client API Repository](https://github.com/canoo/dolphin-platform-js)
* [AngularJS client API Repository](https://github.com/canoo/dolphin-platform-angularjs)
* [Polymer client API Repository](https://github.com/canoo/dolphin-platform-polymer)
* [Maven archetype Repository](https://github.com/canoo/dolphin-platform-spring-boot-archetype)
* [Lazybones template Repository](https://github.com/canoo/dolphin-platform-lazybones-templates)

## 🆕🐥:dog: First Timers Support
At Canoo we :heart: open source. Based on this we develop the complete Dolphin Platform framework as an open source project. If you are new to open source development we have some specific issues for you. Just have a look at [all issues with the 'good first issue' label](https://github.com/canoo/dolphin-platform/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22). This issues are reserved for people who never contributed to Open Source before. We know that the process of creating a pull request is the biggest barrier for new contributors. This issues are for you 💝 We use such issues for example to get involved at [Hacktoberfest](https://hacktoberfest.digitalocean.com). If you want to know more about open source development and contribution in general you should have a look at [this overview](https://opensource.guide).

## License
The project is released as open source under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

<br/><br/>
<p align="center">
<sub>About Canoo</sub>
</p>
<p align="center">
<a title="Canoo Website" href="http://www.canoo.com/"><img style="margin:12px !important;" src="http://www.guigarage.com/wordpress/wp-content/uploads/2016/08/color-link-48-1.png"/></a>
<a title="Canoo at Twitter" href="https://twitter.com/canoo"><img style="margin:12px !important;" src="http://www.guigarage.com/wordpress/wp-content/uploads/2016/08/color-twitter-48-1.png"/></a>
<a title="Canoo at LinkedIn" href="https://www.linkedin.com/company/canoo-engineering-ag"><img style="margin:12px !important;" src="http://www.guigarage.com/wordpress/wp-content/uploads/2016/08/color-linkedin-48-1.png"/></a>
<a title="Canoo at Xing" href="https://www.xing.com/companies/canooengineeringag"><img style="margin:12px !important;" src="http://www.guigarage.com/wordpress/wp-content/uploads/2016/08/xing-48-1.png"/></a>
<a title="Canoo at YouTube" href="https://www.youtube.com/user/canoovideo"><img style="margin:12px !important;" src="http://www.guigarage.com/wordpress/wp-content/uploads/2016/08/color-youtube-48-1.png"/></a>
<a title="Canoo at GitHub" href="https://github.com/canoo"><img style="margin:12px !important;" src="http://www.guigarage.com/wordpress/wp-content/uploads/2016/08/color-github-48-1.png"/></a>
<a title="Contact Canoo" href="mailto:info@canoo.com"><img style="margin:12px !important;" src="http://www.guigarage.com/wordpress/wp-content/uploads/2016/08/color-forwardtofriend-48-1.png"/></a>
</p>
