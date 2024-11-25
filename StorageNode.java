import java.io.Serializable;


abstract class StorageNode implements Serializable{
    protected static final String truePath = "C:/Users/abhin/Desktop/System";  // change this to your desired path
    protected final String name;
    protected String path;  //including the file/folder name
    protected String label;
    protected Boolean is_encrypted;
    protected String actualPath;

    public StorageNode(String name, String path) {
        this.name = name;
        this.path = path;
        this.label = "";
        this.is_encrypted = false;
        this.actualPath = truePath + this.path;
    }
    public String getPath(){
        return this.actualPath;
    }
    public Boolean command_status() {
        return this.is_encrypted;
    }
    public void command_addLabel(String label) {
        this.label = label;
    }
    public void command_deleteLabel() {
        this.label = "";
    }
    abstract public boolean command_encrypt(String passkey);
    abstract public boolean command_decrypt(String passkey);
    abstract public void command_cp(String path);
    abstract public void command_mv(String path);
    
}
