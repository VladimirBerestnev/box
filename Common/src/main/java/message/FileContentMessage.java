package message;

public class FileContentMessage extends Message{

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private long startPosition;
    private byte [] content;
    private boolean last;

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
