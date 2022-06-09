import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.*;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.*;


public class FirstServerHandler extends SimpleChannelInboundHandler<Message> {
    private RandomAccessFile accessFile = null;
    private Connection connection;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New active channel");
        TextMessage answer = new TextMessage();
        answer.setText("Successfully connection");
        ctx.writeAndFlush(answer);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException, SQLException {
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println("incoming text message: " + message.getText());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof DateMessage) {
            DateMessage message = (DateMessage) msg;
            System.out.println("incoming date message: " + message.getDate());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof AuthMessage) {
            connection = DriverManager.getConnection("jdbc:sqlite:box.db");

            AuthMessage message = (AuthMessage) msg;
            System.out.println("incoming auth message: " + message.getLogin() + " " + message.getPassword());
            String check = checkUser(message.getLogin(), message.getPassword());
            if (check != null){
                ctx.writeAndFlush(message);
            } else {
                ErrorMessage em = new ErrorMessage();
                ctx.writeAndFlush(em);
            }

        }

        if (msg instanceof FileRequestMessage) {
            FileRequestMessage frm = (FileRequestMessage) msg;
            if (accessFile == null) {
                final File file = new File(frm.getPath());
                String path = frm.getPath();
                accessFile = new RandomAccessFile(file, "r");
                sendfile(ctx, path);
            }
        }
    }

    private String checkUser(String login, String password) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("select id from users where login = ? AND password = ?")) {
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1));
                return rs.getString(1);

            }
        }
        return null;
    }


    private void sendfile(ChannelHandlerContext ctx, String path) throws IOException {
        if (accessFile != null) {

                final byte[] fileContent;
                final long available = accessFile.length() - accessFile.getFilePointer();
                if (available > 64 * 1024) {
                    fileContent = new byte[64 * 1024];
                } else {
                    fileContent = new byte[(int) available];
                }
                final FileContentMessage message = new FileContentMessage();
                message.setPath(path);
                message.setStartPosition(accessFile.getFilePointer());
                accessFile.read(fileContent);
                message.setContent(fileContent);
                final boolean last = accessFile.getFilePointer() == accessFile.length();
                message.setLast(last);
                ctx.channel().writeAndFlush(message).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!last){
                    sendfile(ctx, path);
                }}
            });
                if (last){
                    accessFile.close();
                    accessFile = null;
                }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws IOException {
        System.out.println("client disconnect");
        if (accessFile != null){
            accessFile.close();
        }
    }
}
