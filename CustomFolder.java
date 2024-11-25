import java.util.ArrayList;
import java.util.HashMap;
public class CustomFolder extends StorageNode{
    
    HashMap<String,CustomFolder> child_folders;
    HashMap<String,CustomFile> child_files;

    public CustomFolder(String name,String path){
        super(name,path);
        child_folders = new HashMap<>();
        child_files = new HashMap<>();
        this.command_mkdir();
    }

    public native void command_ls();
    public native void command_lsWithOption(String option);
    public native void command_mkdir();
    public native void command_rmdir();
    public boolean command_search(String option,String label,ArrayList<StorageNode> nodes){
        boolean found = false;
        // Search in current directory
        for(CustomFile file : child_files.values()) {
            if(file.label.equals(label)) {
                nodes.add(file);
                found = true;
            }
        }
        for(CustomFolder folder : child_folders.values()) {
            if(folder.label.equals(label)) {
                nodes.add(folder);
                found = true;
            }
        }
        // If recursive option (-r), search in subdirectories
        if(option.equals("-r")) {
            for(CustomFolder folder : child_folders.values()) {
                // Use same option (-r) when recursing
                if(folder.command_search("-r", label, nodes)) {
                    found = true;
                }
            }
        }
        return found;
    }
    public ArrayList<StorageNode> command_filter(){
        ArrayList<StorageNode>  filterNodes = new ArrayList<>();
        for (CustomFolder folder : child_folders.values()) {
            if(folder.command_status()){
                filterNodes.add(folder);
            }
        }
        for (CustomFile file : child_files.values()) {
            if(file.command_status()){
                filterNodes.add(file);
            }
        }
        return filterNodes;
    }
    public ArrayList<StorageNode> command_filter(String extension){
        ArrayList<StorageNode>  filterNodes = new ArrayList<>();
        for(CustomFile file : child_files.values()){
            if(file.command_status() && file.getFile_type().equals(extension)){
                filterNodes.add(file);
            }
        }
        return filterNodes;
    }
    public native void command_cp(String path);
    public native void command_mv(String path);
    public boolean command_encrypt(String passkey) {
        boolean success = true;
        for (CustomFile file : child_files.values()) {
            if (!file.command_encrypt(passkey)) {
                success = false; // Mark as failure if encryption fails for any file
            }
        }
        for (CustomFolder folder : child_folders.values()) {
            if (!folder.command_encrypt(passkey)) {
                success = false; // Mark as failure if encryption fails for any subfolder
            }
        }
        if (success) {
            this.is_encrypted = true; // Mark folder as encrypted only if all contents are successfully encrypted
        }
        return success;
    }
    
    public boolean command_decrypt(String passkey) {
        boolean success = true;
        for (CustomFile file : child_files.values()) {
            if (!file.command_decrypt(passkey)) {
                success = false; // Mark as failure if decryption fails for any file
            }
        }
        for (CustomFolder folder : child_folders.values()) {
            if (!folder.command_decrypt(passkey)) {
                success = false; // Mark as failure if decryption fails for any subfolder
            }
        }
        if (success) {
            this.is_encrypted = false; // Mark folder as decrypted only if all contents are successfully decrypted
        }
        return success;
    }
    

}
