/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.factory;

import org.apache.activemq.cli.ConfigurationException;
import org.apache.activemq.dto.BrokerDTO;
import org.apache.activemq.dto.ServerDTO;
import org.apache.activemq.integration.Broker;
import org.apache.activemq.spi.core.security.ActiveMQSecurityManager;
import org.apache.activemq.utils.FactoryFinder;

import java.io.IOException;
import java.net.URI;

public class BrokerFactory
{

   public static BrokerDTO createBrokerConfiguration(URI configURI) throws Exception
   {
      if (configURI.getScheme() == null)
      {
         throw new ConfigurationException("Invalid configuration URI, no scheme specified: " + configURI);
      }

      BrokerFactoryHandler factory = null;
      try
      {
         FactoryFinder finder = new FactoryFinder("META-INF/services/org/apache/activemq/broker/");
         factory = (BrokerFactoryHandler)finder.newInstance(configURI.getScheme());
      }
      catch (IOException ioe )
      {
         throw new ConfigurationException("Invalid configuration URI, can't find configuration scheme: " + configURI.getScheme());
      }


      return factory.createBroker(configURI);
   }

   public static BrokerDTO createBrokerConfiguration(String configuration) throws Exception
   {
      return createBrokerConfiguration(new URI(configuration));
   }

   public static Broker createServer(ServerDTO brokerDTO, ActiveMQSecurityManager security) throws Exception
   {
      if (brokerDTO.configuration != null)
      {
         BrokerHandler handler;
         URI configURI = new URI(brokerDTO.configuration.replace("\\", "/"));
         try
         {
            FactoryFinder finder = new FactoryFinder("META-INF/services/org/apache/activemq/broker/server/");
            handler = (BrokerHandler)finder.newInstance(configURI.getScheme());
         }
         catch (IOException ioe )
         {
            throw new ConfigurationException("Invalid configuration URI, can't find configuration scheme: " + configURI.getScheme());
         }

         return handler.createServer(brokerDTO, security);
      }
      return null;
   }

}
