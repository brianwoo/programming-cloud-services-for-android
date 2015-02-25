/**
 * 
 */
package com.peerasgn3.echoserver;



import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * @author bwoo
 *
 */
public class EchoServer
{

	
	public class EchoServerHander extends SimpleChannelHandler
	{
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) 
		{
			Channel ch = e.getChannel();
			ch.write(e.getMessage());
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) 
		{
			e.getCause().printStackTrace();
	        
			Channel ch = e.getChannel();
		    ch.close();
		}
	}
	
	
	
	
	
	public static void main(String[] args) throws Exception 
	{
        ChannelFactory factory =
             new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                     Executors.newCachedThreadPool());

         ServerBootstrap bootstrap = new ServerBootstrap(factory);

         bootstrap.setPipelineFactory(new ChannelPipelineFactory()
         {
			
			@Override
			public ChannelPipeline getPipeline() throws Exception
			{
				EchoServer echoServer = new EchoServer();
				EchoServerHander echoServerHander = echoServer.new EchoServerHander();
				return Channels.pipeline(echoServerHander);
			}
         });

         bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
 
        bootstrap.bind(new InetSocketAddress(8080));
     }
	
}
