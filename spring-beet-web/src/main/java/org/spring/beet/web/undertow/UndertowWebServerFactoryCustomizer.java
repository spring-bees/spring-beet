/**
 * Copyright © 2020 Lei Zhang (zhanglei@apache.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.spring.beet.web.undertow;

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public class UndertowWebServerFactoryCustomizer
    implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

  int bufferPool;
  String serverHost;
  int serverSecondaryPort;

  public UndertowWebServerFactoryCustomizer(
      int bufferPool, String serverHost, int serverSecondaryPort) {
    this.bufferPool = bufferPool;
    this.serverHost = serverHost;
    this.serverSecondaryPort = serverSecondaryPort;
  }

  @Override
  public void customize(UndertowServletWebServerFactory factory) {
    factory.addDeploymentInfoCustomizers(
        deploymentInfo -> {
          WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
          webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, this.bufferPool));
          deploymentInfo.addServletContextAttribute(
              "io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
        });
    if (this.serverSecondaryPort > 0) {
      factory
          .getBuilderCustomizers()
          .add(
              builder -> {
                builder.addHttpListener(this.serverSecondaryPort, this.serverHost);
              });
    }
  }
}
