package com.hyperdata.bifromq.plugin;


import com.baidu.bifromq.plugin.eventcollector.Event;
import com.baidu.bifromq.plugin.eventcollector.IEventCollector;
import com.baidu.bifromq.plugin.eventcollector.OutOfTenantResource;
import com.baidu.bifromq.plugin.eventcollector.distservice.Delivered;
import com.baidu.bifromq.plugin.eventcollector.distservice.Disted;
import com.baidu.bifromq.plugin.eventcollector.mqttbroker.channelclosed.ChannelClosedEvent;
import com.baidu.bifromq.plugin.eventcollector.mqttbroker.clientdisconnect.ClientDisconnectEvent;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public final class HdcEventLogger implements IEventCollector {
    private static final Logger LOG = LoggerFactory.getLogger("HdcEventLogger");

    @Override
    public void report(Event<?> event) {
        switch (event.type()) {
            case DELIVERED:
                var delivered = (Delivered) event;
                var topic = delivered.messages().getTopic();
                delivered.messages().getMessageList().stream().forEach(c->{
                    
                    c.getMessageList().forEach(x->{
                        LOG.info(String.format("Type = %s, TenantId: %s, Topic = %s, Message = %s"
                                                , event.type().name()
                                                , c.getPublisher().getTenantId()
                                                , topic
                                                , x.getPayload().toStringUtf8()));
                    });
                    

                });
                

                break;  
            case DISTED:
                var disted = (Disted) event;
                
                disted.messages().forEach(c->{
                    
                    c.getMessagePackList().forEach(x->{
                        x.getMessageList().stream().forEach(y->{

                            LOG.info(String.format("Type = %s, TenantId: %s, Topic = %s, Message = %s"
                                                , event.type().name()
                                                , c.getPublisher().getTenantId()
                                                , x.getTopic()
                                                , y.getPayload().toStringUtf8()));
                        });
                        
                    });
                    

                });
                
                break;
        
            default:
            LOG.info(String.format("Unhandled message Type "+ event.type().name()));
        }
        if (LOG.isDebugEnabled()) {
            switch (event.type()) {
                case DISCARD,
                     WILL_DIST_ERROR,
                     QOS0_DIST_ERROR,
                     QOS1_DIST_ERROR,
                     QOS2_DIST_ERROR,
                     OVERFLOWED,
                     QOS0_DROPPED,
                     QOS1_DROPPED,
                     QOS2_DROPPED,
                     OVERSIZE_PACKET_DROPPED,
                     MSG_RETAINED_ERROR,
                     DELIVER_ERROR -> LOG.debug("Message dropped due to {}", event.type());
                default -> {
                    if (event instanceof ChannelClosedEvent || event instanceof ClientDisconnectEvent) {
                        LOG.debug("Channel closed due to {}", event.type());
                    }
                }
            }
        } else if (LOG.isWarnEnabled()) {
            if (event instanceof OutOfTenantResource throttled) {
                LOG.warn("Out of tenant resource: {}", throttled.reason());
            }
        }
    }
}
