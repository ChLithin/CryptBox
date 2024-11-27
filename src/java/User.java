import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;

class User implements Serializable{
    private String userName;
    private String password;
    private String email;
    String currentPath;
    HashMap<String, CustomFolder> folders; //map between path and folder object

    CustomFolder rootDirectory;
    CustomFolder sharedFolder;

    public User(String userNameString, String passwordString, String emailString){
        this.userName = userNameString;
        this.password = passwordString;
        this.email = emailString;
        this.currentPath = "/"+userName;
        this.folders = new HashMap<>();
        this.rootDirectory = new CustomFolder(this.userName, "/"+this.userName);
        this.folders.put(currentPath, this.rootDirectory);
        this.sharedFolder = new CustomFolder("Shared", this.rootDirectory.path+"/Shared");
        this.folders.put(sharedFolder.path, sharedFolder);
        rootDirectory.child_folders.put("Shared", this.sharedFolder);
    }
    public void FileSystem(Scanner scanner, HashMap<String,User> users){
        
        CLI.CLIhandler(this,scanner, users);
    }
    public double getFreeSpace(){
        File file = new File("/");
        return file.getFreeSpace()/(1024*1024*1024);//in GB
    }

    public String getUserName(){
        return this.userName;
    }
    public String getPassword(){
        return this.password;
    }
    public String getEmail(){
        return this.email;
    }
}
