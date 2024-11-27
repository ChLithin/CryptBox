import java.util.HashMap;
import java.util.ArrayList;
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
    public boolean command_status(){
        // First, check if the folder is empty
        if(child_files.isEmpty() && child_folders.isEmpty()){
            return false;
        }
        // Check if all child files are encrypted
        for(CustomFile childFile : child_files.values()){
            if(!childFile.command_status()){
                return false;
            }
        }
        // Check if all child folders are encrypted
        for(CustomFolder childFolder : child_folders.values()){
            if(!childFolder.command_status()){
                return false;
            }
        }
        return true;
    }
    public void helper_rmdir(CustomFolder parent, HashMap<String,CustomFolder> folders){
        this.command_rmdir();
        parent.child_folders.remove(this.name);
        folders.remove(this.path);
        for(CustomFolder child : child_folders.values()){
            folders.remove(child.path);
        }
    }
    public void helper_cp(CustomFolder parent, HashMap<String, CustomFolder> userFolders) {
        CustomFolder copiedFolder = new CustomFolder(this.name, parent.path + "/" + this.name);
    
        for (CustomFile file : this.child_files.values()) {
            file.helper_cp(copiedFolder);
        }
        
        for (CustomFolder childFolder : this.child_folders.values()) {
            childFolder.helper_cp(copiedFolder, userFolders);
        }
        
        parent.child_folders.put(copiedFolder.name, copiedFolder);
        userFolders.put(copiedFolder.path, copiedFolder);
    }
    public void helper_mv(CustomFolder currentParent, CustomFolder newParent, HashMap<String, CustomFolder> folders){
        this.helper_cp(newParent, folders);
        this.helper_rmdir(currentParent, folders);
    }

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
        return success;
    }
}
