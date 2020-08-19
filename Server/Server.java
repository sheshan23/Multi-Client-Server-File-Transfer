import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	static String message = "Final Advanced System Programming Project in Java\n"
			+ "Multi-Client Server file transfer java application\n"
			+ "Developed By: Sheshan Patel and Sunny Patel.\n"
			+ "Hi, I am Server. Good to see you Client.\n";
	
	static String instruction = "Enter the request below:\n"
	+"1) get <fileName>\n"					// Command to download file from server
	+"2) put <fileName>\n"					// Command to upload file to server
	+"3) quit\n"							// Command to disconnect to server
	+ ">>>";
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("This is Server");
		ServerSocket serversocket = new ServerSocket(3000);				//start server at 3000 port
		
		System.out.println("Server stared successfully.");
		
		while(true)
		{
			Socket socket ;
			socket=serversocket.accept();

			ClientInstances clientinstances = new ClientInstances(socket);
			clientinstances.start();
		}
	}

/* isFileExist() function is to check the file existence on the server storage*/	
public static boolean isFileExist(String fileName) {
	
	boolean isFile = false;
	File file = new File(fileName);
	isFile = file.exists();
	return isFile;
}

/* downloadFile() function is to save client uploaded file in the server storage */
public static void downloadFile (String fileName, byte[] fileBytes) 
		throws Exception {
	
	File file = new File(fileName);
	FileOutputStream downloadFile = new FileOutputStream(file);
	downloadFile.write(fileBytes);
	downloadFile.flush();
	downloadFile.close();
}

/* uploadFile() function is to send the file that is requested by the client  */
public static byte[] uploadFile (String fileName)
		throws Exception {
	
	File file = new File(fileName);
	FileInputStream uploadFile = new FileInputStream(file);
	byte[] fileBytes = new byte[(int) file.length()];
	uploadFile.read(fileBytes);
	uploadFile.close();
	
	return fileBytes;
}

/* handle_request() function is to handle upload or download request from the client*/
public static void handle_request (String request, 
		DataInputStream dis, DataOutputStream dos) throws Exception {
	
	String[] request_parts = request.split(" ");
	
	if (request_parts[0].equals("get")) {
		
		if (isFileExist(request_parts[1])) {
			
			dos.writeUTF("File exists");
			dos.flush();
			
			byte[] fileBytes = uploadFile(request_parts[1]);
			
			dos.writeInt(fileBytes.length);
			dos.flush();
			
			dos.write(fileBytes);
			dos.flush();
			System.out.println("File Uploaded Successfully");
		}
		
		else {
			dos.writeUTF("File doesn't exists in Server. Please enter other filename.");
			dos.flush();
		}
	}
	
	else if (request_parts[0].equals("put")) {
		
		byte[] fileBytes = new byte[dis.readInt()];
		dis.readFully(fileBytes);
		 
		downloadFile(request_parts[1], fileBytes);
		System.out.println("File Downloaded Successfully");
	}
	
	else {
		dos.writeUTF("Wrong input.\n" + instruction);
		dos.flush();
	}

 }

}
