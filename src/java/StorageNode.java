import java.io.Serializable;


abstract class StorageNode implements Serializable{
    protected static final String truePath = "C:\\CryptBox\\System";  // change this to your desired path
    private static final long serialVersionUID = 1L;
    protected final String name;
    protected String path;  //including the file/folder name
    protected String label;
    protected String actualPath;

    public StorageNode(String name, String path) {
        this.name = name;
        this.path = path;
        this.label = "";
        this.actualPath = truePath  + this.path;
    }
    public void command_addLabel(String label) {
        this.label = label;
    }
    public void command_deleteLabel() {
        this.label = "";
    }
    public void updatePath(String path) {
        this.path = path;
        this.actualPath = truePath + this.path;
    }
    abstract public boolean command_encrypt(String passkey);
    abstract public boolean command_decrypt(String passkey);
    
}
