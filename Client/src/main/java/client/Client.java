package client;

import handler.JsonDecoder;
import handler.JsonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import message.FileContentMessage;
import message.FileRequestMessage;
import message.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class Client {

    private SocketChannel channel;

    public Client() {
        new Thread(()-> {

        final NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            channel = ch;
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {

//                                                AuthMessage authMessage = new AuthMessage();
//
//                                                authMessage.setLogin(String.format("%s", loginField.getText().trim()));
//                                                authMessage.setPassword(String.format("%s", passwordField.getText().trim()));
//                                                passwordField.clear();
//                                                ctx.writeAndFlush(authMessage);




                                        }

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws FileNotFoundException {


  //                                              if (msg instanceof AuthMessage){

                                        //        }

                                                System.out.println("receive msg " + msg);


                                                if (msg instanceof FileContentMessage) {

                                                    FileContentMessage fcm = (FileContentMessage) msg;
                                                    String receiver = fcm.getPath();
                                                    String name = receiver.substring(receiver.lastIndexOf("\\")+1, receiver.length());
                                                    try (final RandomAccessFile accessFile = new RandomAccessFile("Data\\" + name, "rw")) {
                                                        accessFile.seek(fcm.getStartPosition());
                                                        accessFile.write(fcm.getContent());
                                                        if (fcm.isLast()) {
                                      //                      ctx.close();
                                                        }

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                        }
                                    }
                            );
                        }
                    });

            System.out.println("com.client.Client started");

            Channel channel = bootstrap.connect("localhost", 9000).sync().channel();


            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        }).start();
    }

    public void sendFileClient(FileRequestMessage frm){
        channel.writeAndFlush(frm);
    }


}
