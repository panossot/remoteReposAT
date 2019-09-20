/**
 * Copyright 2018 Red Hat, Inc, and individual contributors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.remote.tests.microprofile;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.remote.tests.microprofile.jwtauth.TestApplication;
import org.remote.tests.microprofile.jwtauth.providers.JWTTestResource;

import javax.ws.rs.core.MediaType;

import static org.remote.tests.microprofile.utils.jwt.TokenUtils.createToken;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 7/10/18
 */
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/master/thorntail/src/main/java","modules/testcases/jdkAll/server1/basic/src/main/java","modules/testcases/jdkAll/server2/basic/src/main/java"})
@RunWith(Arquillian.class)
@RunAsClient
public class JWTAndOpentracingIntegrationTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return initDeployment().addAsResource("project-no-roles-props.yml", "project-defaults.yml");
    }

    protected static WebArchive initDeployment() {
        WebArchive deployment = ShrinkWrap.create(WebArchive.class);
        deployment.addClass(JWTTestResource.class);
        deployment.addClass(TestApplication.class);
        deployment.addAsManifestResource(new ClassLoaderAsset("keys/public-key.pem"), "/MP-JWT-SIGNER");
        return deployment;
    }

   
    @Test
    public void shouldRejectPostWithRBACFilterWithTracingEnabled() throws Exception {
        HttpResponse response = Request.Post("http://localhost:8080/mpjwt/providers")
                .setHeader("Accept", MediaType.TEXT_PLAIN)
                .execute()
                .returnResponse();
        Assert.assertEquals(401, response.getStatusLine().getStatusCode());
    }


    @Test
    public void shouldWorkWithAuthedPostWithTracingEnabled() throws Exception {
        String response = Request.Post("http://localhost:8080/mpjwt/providers")
                .setHeader("Authorization", "Bearer " + createToken("MappedRole2"))
                .setHeader("Accept", MediaType.TEXT_PLAIN)
                .execute().returnContent().asString();
        Assert.assertEquals("POST response", response);
    }
}
