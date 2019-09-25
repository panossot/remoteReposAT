package org.remote.tests.microprofile.jwtauth;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/master/thorntail/src2/main/java"})
@ApplicationScoped
@ApplicationPath("/mpjwt")
@LoginConfig(authMethod = "MP-JWT", realmName = "testSuiteRealm")
public class TestApplication extends Application {
    // intentionally left empty
}
