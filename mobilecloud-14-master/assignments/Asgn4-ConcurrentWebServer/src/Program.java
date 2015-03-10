/**
 * 
 */




import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * This is the program implemented for the Assignment 3.
 * 
 * @author bwoo
 *
 */
public class Program
{
	private static final String CHILD_KEEP_ALIVE = "child.keepAlive";
	private static final String CHILD_TCP_NO_DELAY = "child.tcpNoDelay";
	private EchoServerAcceptor acceptor;
	private ServerBootstrap bootstrap;

	/**
	 * Echo Server Constructor.  
	 */
	public Program()
	{
	
		// Half-Sync/Half Async pattern: where the boss thread will accept the incoming
		// connection and forward the connection to the worker thread pool.
		ChannelFactory factory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
		
        // The ServerBootstrap is acting as a reactor which is dispatching
        // the service requests to the EchoServerAcceptor.
		
		// This is just to create the acceptor and the ServerBootstrap reactor.  
		// At this point, we are not connect the acceptor to the 
		// ServerBootstrap yet because we might have other handlers 
		// we can hookup.
		this.acceptor = new EchoServerAcceptor();
        this.bootstrap = new ServerBootstrap(factory);
	}
	

	/**
	 * Set TCP No Delay
	 * 
	 * @param bool
	 */
	public void setTcpNoDelay(boolean bool)
	{
		// a Wrapper Facade method to abstract out child.tcpNoDelay from the user.
		bootstrap.setOption(CHILD_TCP_NO_DELAY, bool);
	}
	
	
	/**
	 * Set KeepAlive
	 * 
	 * @param bool
	 */
	public void setKeepAlive(boolean bool)
	{
		// a Wrapper Facade method to abstract out child.keepAlive from the user.
		bootstrap.setOption(CHILD_KEEP_ALIVE, bool);
	}
	
	
	
	/**
	 * This method is to start the Echo Server
	 * 
	 */
	public void start(int port)
	{
		// bootstrap (reactor) will now connect the acceptor and binding port.
		// this also hide the InetSocketAddress away from the user.
		
		// the reactor should now be able to forward connection 
		// requests to the handler (using the acceptor).
        bootstrap.setPipelineFactory(acceptor);
		bootstrap.bind(new InetSocketAddress(port));
	}
	
	
	/**
	 * Adding handler to the acceptor.
	 * @param handler
	 */
	public void addHandler(SimpleChannelHandler handler)
	{
		this.acceptor.addHandler(handler);
	}
	
	
	
	
	/*****************************************************************
	 * EchoServerAcceptor - this class acts as the acceptor and 
	 * is used to instantiate the appropriate handler to handle 
	 * channel requests.
	 * 
	 * @author bwoo
	 *
	 ******************************************************************/
	public class EchoServerAcceptor implements ChannelPipelineFactory
	{
		List<SimpleChannelHandler> handlers = new ArrayList<SimpleChannelHandler>();
		
		@Override
		public ChannelPipeline getPipeline() throws Exception
		{			
			ChannelPipeline pipeline = Channels.pipeline();
			
			for (SimpleChannelHandler handler : handlers)
			{
				pipeline.addLast(handler.toString(), handler);
			}
			
			return pipeline;
		}
		
		
		/**
		 * Add Handler to the Acceptor.
		 * 
		 * @param handler
		 */
		private void addHandler(SimpleChannelHandler handler)
		{
			// this is another Wrapper Facade method to hide the complexity 
			// of adding the handler to the Acceptor from the user.
			handlers.add(handler);
		}
		
	}
		
	
	
	/*************************************************
	 * Entry point to start up the EchoServer.
	 * 
	 ************************************************/
	public static void main(String[] args) throws Exception 
	{
		int port;
		
		if (args.length > 0)
			port = new Integer(args[0]);
		else
		{
			System.err.println("Usage: Program [port]");
			return;
		}
		
		
		// We setup the EchoServer's configurations and start at the end.
		// Notice that the Wrapper Facade technique is applied to hide
		// a lot of the implementation details, such as InetSocketAddress, 
		// child.keepAlive, child.tcpNoDelay, etc.
		Program echoServer = new Program();
		echoServer.addHandler(new EchoServerHander());
		echoServer.setKeepAlive(true);
		echoServer.setTcpNoDelay(true);
		
		echoServer.start(port);

     }
	
}


/********************************************************************************
 * EchoServerHandler - This is the handler that the user will have to create
 * to support the "echo" functionality.  This gives the Inversion of Control (IOC)
 * to the user.
 * 
 * @author bwoo
 *
 ********************************************************************************/
class EchoServerHander extends SimpleChannelHandler
{
	
	/**
	 * Hook method to override to support the echo functionality.
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) 
	{
		Channel ch = e.getChannel();
		ch.write(e.getMessage());
	}
	
	
			
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {

		super.channelOpen(ctx, e);		
		System.out.println("channel opened... connected");
		
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) 
	{
		e.getCause().printStackTrace();
        
		Channel ch = e.getChannel();
	    ch.close();
	}
}


