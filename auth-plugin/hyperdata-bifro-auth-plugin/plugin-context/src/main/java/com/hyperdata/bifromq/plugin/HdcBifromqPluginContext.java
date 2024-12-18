package com.hyperdata.bifromq.plugin;

import com.baidu.bifromq.plugin.BifroMQPluginContext;
import com.baidu.bifromq.plugin.BifroMQPluginDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdcBifromqPluginContext extends BifroMQPluginContext{
    private static final Logger log = LoggerFactory.getLogger(HdcBifromqPluginContext.class);

    public HdcBifromqPluginContext (BifroMQPluginDescriptor descriptor){
        super(descriptor);
    }

    @Override
    protected void init() {
        log.info("TODO: Initialize your plugin context using descriptor {}", descriptor);
    }

    @Override
    protected void close() {
        log.info("TODO: Close your plugin context");
    }
}