package com.kevinherron;

import java.util.concurrent.TimeUnit;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.resolver.DefaultAddressResolverGroup;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.InternalThreadLocalMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayHook extends AbstractGatewayModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final NioEventLoopGroup eventLoop = new NioEventLoopGroup(0);

    @Override
    public void setup(GatewayContext gatewayContext) {
        logger.info("setup()");
    }

    @Override
    public void startup(LicenseState licenseState) {
        logger.info("startup()");

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoop)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        logger.info("initChannel");
                    }
                });

            bootstrap.connect("localhost", 1234).get(2, TimeUnit.SECONDS);
        } catch (Throwable t) {
            logger.error("failed getting un-gettable endpoints: {}", t.getMessage(), t);
        }
    }

    @Override
    public void shutdown() {
        logger.info("shutdown()");

        try {
            eventLoop.shutdownGracefully().get();
        } catch (Throwable e) {
            logger.error("Error waiting for event loop shutdown: {}", e.getMessage(), e);
        }
        try {
            GlobalEventExecutor.INSTANCE.shutdownGracefully().get();
        } catch (Throwable e) {
            logger.error("Error waiting for GlobalEventExecutor shutdown: {}", e.getMessage(), e);
        }
        try {
            DefaultAddressResolverGroup.INSTANCE.close();
        } catch (Throwable e) {
            logger.error("Error closing DefaultAddressResolverGroup: {}", e.getMessage(), e);
        }
        InternalThreadLocalMap.destroy();
        FastThreadLocal.removeAll();
    }

}
