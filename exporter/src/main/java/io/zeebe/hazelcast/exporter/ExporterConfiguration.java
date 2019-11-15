package io.zeebe.hazelcast.exporter;

import io.zeebe.protocol.record.ValueType;

public class ExporterConfiguration {

  public int port = 5701;

  public String topicPrefix = "zeebe-";

  public String enabledValueTypes = "JOB,WORKFLOW_INSTANCE,DEPLOYMENT,INCIDENT";

  public String enabledRecordTypes = "EVENT";

  public boolean updatePosition = true;

  public boolean liteMember = false;

  public String queuePrefix = "zeebe-exporter-queue";

  public boolean oneQueue = true;

  public String getTopicName(ValueType valueType) {
    return topicPrefix + valueType.name();
  }

  public String getQueueName(ValueType valueType) {
    return oneQueue ? queuePrefix : queuePrefix + "-" + valueType.name();
  }

  @Override
  public String toString() {
    return "[port="
        + port
        + ", topicPrefix="
        + topicPrefix
        + ", enabledValueTypes="
        + enabledValueTypes
        + ", enabledRecordTypes="
        + enabledRecordTypes
        + ", updatePosition="
        + updatePosition
        + ", liteMember="
        + liteMember
        + "]";
  }
}
