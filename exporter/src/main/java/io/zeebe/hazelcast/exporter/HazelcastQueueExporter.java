package io.zeebe.hazelcast.exporter;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IQueue;
import io.zeebe.exporter.api.context.Controller;
import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.ValueType;

import java.util.EnumMap;

/**
 * @author a.novikov
 * @date 15/11/2019
 */
public class HazelcastQueueExporter extends HazelcastExporter {

    private final EnumMap<ValueType, IQueue<byte[]>> queues = new EnumMap<>(ValueType.class);

    @Override
    public void open(Controller controller) {
        this.controller = controller;

        final Config cfg = new Config();
        cfg.getNetworkConfig().setPort(config.port);
        cfg.setProperty("hazelcast.logging.type", "slf4j");
        cfg.setLiteMember(config.liteMember);

        hazelcast = Hazelcast.newHazelcastInstance(cfg);

        enabledValueTypes.forEach(
                valueType -> {
                    final String queueName = config.getQueueName(valueType);
                    queues.put(valueType, hazelcast.getQueue(queueName));

                    logger.debug("Export records of type '{}' to Hazelcast topic '{}'", valueType, queueName);
                });
    }

    @Override
    public void export(Record record) {

        final IQueue<byte[]> queue = queues.get(record.getValueType());
        if (queue != null) {

            final byte[] protobuf = transformRecord(record);
            queue.add(protobuf);
        }

        if (config.updatePosition) {
            controller.updateLastExportedRecordPosition(record.getPosition());
        }
    }
}
