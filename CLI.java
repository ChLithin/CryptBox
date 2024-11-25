import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class CLI {
   public static void CLIhandler(User user, Scanner scanner,  HashMap<String,User> users) {
        String trueUser = "C:/Users/abhin/Desktop/System";
        System.out.println("Your File System is ready \n");
        scanner.nextLine();//ignoring the first line
        while(true){
            CustomFolder currentFolder = user.folders.get(user.currentPath);
            System.out.print(user.currentPath + " % ");
            String input = scanner.nextLine();
            String[] command = input.split(" ");
            if(command[0].equals("ls")){
                if(command.length == 1){
                    currentFolder.command_ls();
                }
                else if(command.length == 2){
                    String option = command[1];
                    if(option.equals("-l")){
                        currentFolder.command_lsWithOption("-l");
                    }
                    else{
                        System.out.println("Invalid option for ls command");
                    }
                }
                else{
                    System.out.println("Invalid arguments for ls command");
                }
            }
            else if(command[0].equals("cd")){
                if(command.length == 1){
                    user.currentPath = user.rootDirectory.path;
                }
                else if(command.length == 2){
                    if(command[1].equals("..")){
                        int toSlice = user.currentPath.length() - currentFolder.name.length() - 1;
                        user.currentPath = user.currentPath.substring(0, toSlice);
                    }
                    else{
                        String newPath = user.currentPath + "/" + command[1];
                        if(user.folders.containsKey(newPath)){
                            user.currentPath = newPath;
                        }
                        else{
                            System.out.println("Directory not found");
                        }
                    }
                } 
                else{
                    System.out.println("Invalid arguments for cd command");
                }
            }
            else if(command[0].equals("cp")){
                if(command.length == 2){
                    String path = command[1];
                    if(user.folders.containsKey(path)){
                        currentFolder.command_cp(trueUser + path);
                    }
                    else{
                        System.out.println("Invalid path");
                    }
                }
                else if(command.length == 3){
                    String fileName = command[1];
                    String path = command[2];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(file != null){
                        if(user.folders.containsKey(path)){
                            file.command_cp(trueUser + path);
                        }
                        else{
                        System.out.println("Invalid path");
                        }
                    }
                    else{
                        System.out.println("File not found");
                    }
                }
                else{
                    System.out.println("Invalid arguments for cp command");
                }
            }
            else if(command[0].equals("mv")){
                if(command.length == 2){
                    String path = command[1];
                    if(user.folders.containsKey(path)){
                        currentFolder.command_mv(trueUser + path);
                    }
                    else{
                        System.out.println("Invalid path");
                    }
                }
                else if(command.length == 3){
                    String fileName = command[1];
                    String path = command[2];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(file != null){
                        if(user.folders.containsKey(path)){
                            file.command_mv(trueUser + path);
                        }
                        else{
                            System.out.println("Invalid path");
                        }
                    }
                    else{
                        System.out.println("File not found");
                    }
                }
                else{
                    System.out.println("Invalid arguments for mv command");
                }
            }
            else if(command[0].equals("rm")){
                if(command.length == 2){
                    String fileName = command[1];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(!file.command_status()){
                        if(file != null){
                            file.command_rm();
                            currentFolder.child_files.remove(fileName);
                        }
                        else{
                            System.out.println("File not found");
                        }
                    }
                    else{
                        System.out.println("Can't delete an Encrypted file");
                    }
                }
                else if(command.length == 3){
                    String option = command[1];
                    String fileName = command[2];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(!file.command_status()){
                        if(option.equals("-i")){
                            if(file != null){
                                file.command_rmWithOption(option);
                            }
                            else{
                            System.out.println("File not found");
                            }
                        }
                        else{
                        System.out.println("Invalid option for rm command");
                        }
                    }
                    else{
                        System.out.println("Can't delete an Encrypted file");
                    }
                }
                else{
                    System.out.println("Invalid arguments for rm command");
                }   
            }
            else if(command[0].equals("touch")){
                if(command.length == 2){
                    String fileName = command[1];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(!currentFolder.command_status()){
                        if(file!= null){
                            System.out.println("File already exists");
                        }
                        else{
                            file = new CustomFile(fileName,user.currentPath + "/" + fileName);
                            currentFolder.child_files.put(fileName, file);
                        }
                    }
                    else{
                        System.out.println("Can't create a file within an Encrypted Folder");
                    }
                }
                else{
                    System.out.println("Invalid arguments for touch command");
                }
            }
            else if(command[0].equals("cat")){
                if(command.length == 2){
                    String fileName = command[1];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(file != null){
                        file.command_cat();
                    }
                    else{
                        System.out.println("File not found");
                    }
                }
                else{
                    System.out.println("Invalid arguments for cat command");
                }
            }
            else if(command[0].equals("echo")){
                if(command.length == 4){
                    String text = command[1];
                    String option = command[2];
                    if(option.equals(">") || option.equals(">>")){
                        String fileName = command[3];
                        CustomFile file = currentFolder.child_files.get(fileName);
                        if(!file.command_status()){
                            if(file != null){
                                file.command_echo(text,option);
                            }
                            else{
                                System.out.println("File not found");
                            }
                        }
                        else{
                            System.out.println("Can't write to an Encrypted file");
                        }
                    }
                    else{
                        System.out.println("Invalid option for echo command");
                    }
                }
                else{
                    System.out.println("Invalid arguments for echo command");
                }
            }
            else if(command[0].equals("mkdir")){
                if(command.length == 2){
                    String folderName = command[1];
                    CustomFolder folder = currentFolder.child_folders.get(folderName);
                    if(!currentFolder.command_status()){
                        if(folder != null){
                            System.out.println("Folder already exists");
                        }
                        else{
                            folder = new CustomFolder(folderName,user.currentPath + "/" + folderName);
                            currentFolder.child_folders.put(folderName, folder);
                            user.folders.put(folder.path, folder);
                        }
                    }
                    else{
                        System.out.println("Can't create a folder within an Encrypted Folder");
                    }
                }
                else{
                    System.out.println("Invalid arguments for mkdir command");
                }
            }  
            else if(command[0].equals("rmdir")){
                if(command.length == 2){
                    String folderName = command[1];
                    CustomFolder folder = currentFolder.child_folders.get(folderName);
                    if(folder != null){
                        if(!folder.command_status()){
                            folder.command_rmdir();
                            currentFolder.child_folders.remove(folderName);
                            user.folders.remove(folder.path);
                        }
                        else{
                            System.out.println("Can't delete an Encrypted folder");
                        }
                    }
                    else{
                        System.out.println("Folder not found");
                    }
                }
                else{
                    System.out.println("Invalid arguments for rmdir command");
                }
            }
            else if(command[0].equals("search")){
                if(command.length == 3){
                    ArrayList<StorageNode> nodes = new ArrayList<StorageNode>();
                    String option = command[1];
                    String label = command[2];
                    if(option.equals("-r") || option.equals("-i")){
                        if(currentFolder.command_search(option, label, nodes)){
                            System.out.println("Found " + nodes.size() + " results:");
                            for(StorageNode node : nodes){
                                System.out.println(node.name);
                            }
                        }
                        else{
                            System.out.println("No results found");
                        }
                    }
                    else{
                        System.out.println("Invalid option for search command");
                    }
                }
                else{
                    System.out.println("Invalid arguments for search command");
                }
            }
            else if(command[0].equals("filter")){
                ArrayList<StorageNode> filterNodes = new ArrayList<StorageNode>();
                if(command.length == 1){
                    filterNodes = currentFolder.command_filter();
                    System.out.println("Filtered Files/Folders in current directory:");
                    for(StorageNode node : filterNodes){
                        System.out.println(node.name);
                    }
                }
                else if(command.length == 2){
                    String extension = command[1];
                    filterNodes = currentFolder.command_filter(extension);
                    System.out.println("Filtered Files/Folders with extension '" + extension + "' in current directory:");
                    for(StorageNode node : filterNodes){
                        System.out.println(node.name);
                    }
                }
                else{
                    System.out.println("Invalid arguments for filter command");
                }
            }
            else if(command[0].equals("status")){
                if(command.length == 1){
                    System.out.println(currentFolder.command_status());
                }
                else if (command.length == 2){
                    String fileName = command[1];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(file != null){
                        System.out.println("Encryption status : " + file.command_status());
                    }
                    else{
                        System.out.println("File not found");
                    }
                }
                else{
                    System.out.println("Invalid arguments for status command");
                }
            }
            else if(command[0].equals("encrypt")){
                if(command.length == 2){
                    String passkey = command[1];
                    if(!currentFolder.command_status()){
                        if(currentFolder.command_encrypt(passkey)){
                            System.out.println("Encryption successful");
                        }
                        else{
                            System.out.println("Encryption failed");
                        }
                        
                    }
                    else{
                        System.out.println("Folder is already encrypted");
                    }
                }
                else if(command.length == 3){
                    String fileName = command[1];
                    String passkey = command[2];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(file!= null){
                        if(!file.command_status()){
                            if(file.command_encrypt(passkey)){
                                System.out.println("Encryption successful");
                            }
                            else{
                                System.out.println("Encryption failed");
                            }
                        }
                        else{
                            System.out.println("File is already encrypted");
                        }
                    }
                    else{
                        System.out.println("File not found");
                    }
                }
                else{
                    System.out.println("Invalid arguments for encrypt command");
                }
            }
            else if(command[0].equals("decrypt")){
                if(command.length == 2){
                    String passkey = command[1];
                    if(currentFolder.command_status()){
                        if(currentFolder.command_decrypt(passkey)){
                            System.out.println("Decryption successful");
                        }
                        else{
                            System.out.println("Decryption failed");
                        }
                    }
                    else{
                        System.out.println("Folder is not encrypted");
                    }
                }
                else if(command.length == 3){
                    String fileName = command[1];
                    String passkey = command[2];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(file!= null){
                        if(file.command_status()){
                            if(file.command_decrypt(passkey)){
                                System.out.println("Decryption successful");
                            }
                            else{
                                System.out.println("Decryption failed");
                            }
                        }
                        else{
                            System.out.println("File is not encrypted");
                        }
                    }
                    else{
                        System.out.println("File not found");
                    }
                }
                else{
                    System.out.println("Invalid arguments for decrypt command");
                }
            }
            else if(command[0].equals("addlabel")){
                if(command.length == 2){
                    String label = command[1];
                    currentFolder.command_addLabel(label);
                }
                else if(command.length == 3){
                    String fileName = command[1];
                    String label = command[2];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(file != null){
                        file.command_addLabel(label);
                    }
                    else{
                        System.out.println("File not found");
                    }
                }
                else{
                    System.out.println("Invalid arguments for addlabel command");
                }
            }
            else if(command[0].equals("deletelabel")){
                if(command.length == 1){
                    currentFolder.command_deleteLabel();
                }
                else if(command.length == 2){
                    String fileName = command[1];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(file != null){
                        file.command_deleteLabel();
                    }
                    else{
                        System.out.println("File not found");
                    }
                }
                else{
                    System.out.println("Invalid arguments for deletelabel command");
                }
            }
            
            else if(command[0].equals("share")){
                if(command.length == 2){
                    String userName = command[1];
                    if(users.containsKey(userName)){
                        if(!currentFolder.command_status()){
                            currentFolder.command_cp(userName + "/Shared");
                        }
                        else{
                            System.out.println("Can't share an Encrypted folder");
                        }
                    }
                    else{
                        System.out.println("User not found");
                    }
                }
                else if(command.length == 3){
                    String fileName = command[1];
                    String userName  = command[2];
                    CustomFile file = currentFolder.child_files.get(fileName);
                    if(file != null){
                        if(users.containsKey(userName)){
                            if(!file.command_status()){
                                file.command_cp(userName + "/Shared");
                            }
                            else{
                                System.out.println("Can't share an Encrypted file");
                            }
                        }
                        else{
                            System.out.println("User not found");
                        }   
                    }
                    else{
                        System.out.println("File not found");
                    } 
                }
                else{
                    System.out.println("Invalid arguments for share command");
                }
            }
            else if(command[0].equals("space")){
                if(command.length == 1){
                    System.out.println("Free space: "+user.getFreeSpace()+" GB");
                }
                else{
                    System.out.println("Invalid arguments for space command");
                }
            }
            else if(command[0].equals("Logout")){
                if(command.length != 1){
                    System.out.println("Invalid arguments for Logout command");

                }else{
                    System.out.println("Logged out successfully\n");
                    break;
                }
                
            }
            else if(command[0].equals("del")){
                if(command.length == 1){
                    System.out.print("Enter your password: ");
                    String password = scanner.next();
                    if(MainCLI.hashString(password).equals(user.getPassword())){
                        users.remove(user.getUserName());
                        System.out.println("User deleted successfully\n");
                    }
                    else{
                        System.out.println("Incorrect password\n");
                    }
                }
                else{
                    System.out.println("Invalid arguments for del command");
                }
                break;
            }
            else if(command[0].equals("path")){
                System.out.println(currentFolder.getPath());
            }
            else{
                System.out.println("Invalid command");
            }

        }
    }
    
}

