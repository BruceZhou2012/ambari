/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller.logging;

import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.log4j.Logger;

import java.util.List;

public class LoggingRequestHelperFactoryImpl implements LoggingRequestHelperFactory {

  private static final Logger LOG = Logger.getLogger(LoggingRequestHelperFactoryImpl.class);

  private static final String LOGSEARCH_SITE_CONFIG_TYPE_NAME = "logsearch-site";

  private static final String LOGSEARCH_SERVICE_NAME = "LOGSEARCH";

  private static final String LOGSEARCH_SERVER_COMPONENT_NAME = "LOGSEARCH_SERVER";

  private static final String LOGSEARCH_UI_PORT_PROPERTY_NAME = "logsearch.ui.port";


  @Override
  public LoggingRequestHelper getHelper(AmbariManagementController ambariManagementController, String clusterName) {
    Clusters clusters =
      ambariManagementController.getClusters();

    try {
      Cluster cluster = clusters.getCluster(clusterName);
      if (cluster != null) {
        Config logSearchSiteConfig =
          cluster.getDesiredConfigByType(LOGSEARCH_SITE_CONFIG_TYPE_NAME);

        List<ServiceComponentHost> listOfMatchingHosts =
          cluster.getServiceComponentHosts(LOGSEARCH_SERVICE_NAME, LOGSEARCH_SERVER_COMPONENT_NAME);

        if (listOfMatchingHosts.size() == 0) {
          LOG.info("No matching LOGSEARCH_SERVER instances found, this may indicate a deployment problem.  Please verify that LogSearch is deployed and running.");
          return null;
        }

        if (listOfMatchingHosts.size() > 1) {
          LOG.warn("More than one LOGSEARCH_SERVER instance found, this may be a deployment error.  Only the first LOGSEARCH_SERVER instance will be used.");
        }

        ServiceComponentHost serviceComponentHost =
          listOfMatchingHosts.get(0);

        final String logSearchHostName = serviceComponentHost.getHostName();
        final String logSearchPortNumber =
          logSearchSiteConfig.getProperties().get(LOGSEARCH_UI_PORT_PROPERTY_NAME);

        return new LoggingRequestHelperImpl(logSearchHostName, logSearchPortNumber);
      }
    } catch (AmbariException ambariException) {
      LOG.error("Error occurred while trying to obtain the cluster, cluster name = " + clusterName, ambariException);
    }


    return null;
  }
}
