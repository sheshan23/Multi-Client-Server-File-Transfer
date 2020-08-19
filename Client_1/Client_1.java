import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client_1 {
	
	static String instruction = 
			"Enter the request below:\n"
			+"1) get <fileName>\n"
			+"2) put <fileName>\n"
			+"3) quit\n"
			+ ">>>";
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("This is Client");
		Socket socket = new Socket("localhost",3000);			// To connect to the server using it's IpAddress
		
		System.out.println("Client started successfully.");
		
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		
		Scanner scanner = new Scanner(System.in);
		
		String request = "";
		
		System.out.print(dis.readUTF());
		
		while(true)
		{
			request = scanner.nextLine();
			
			if(request.equals("quit")) {
				dos.writeUTF(request);
				System.out.println(dis.readUTF());
				break;
			}
			
			handle_request(request, dis, dos);

			System.out.print(">>>");
		}
		
		scanner.close();
		dis.close();
		dos.close();
		socket.close();
	}

/* isFileExist() function is to check the file existence on the client storage*/		
public static boolean isFileExist(String fileName) {
	
	boolean isFile = false;
	File f = new File(fileName);
	isFile = f.exists();
	return isFile;
}

/* downloadFile() function is to download the file from the server */
public static void downloadFile (String fileName, byte[] fileBytes) 
		throws Exception {
	
	File file = new File(fileName);
	FileOutputStream downloadFile = new FileOutputStream(file);
	downloadFile.write(fileBytes);
	downloadFile.flush();
	downloadFile.close();
}

/* uploadFile() function is to upload the file to the server*/
public static byte[] uploadFile (String fileName)
		throws Exception {
	
	File file = new File(fileName);
	FileInputStream uploadFile = new FileInputStream(file);
	byte[] fileBytes = new byte[(int) file.length()];
	uploadFile.read(fileBytes);
	uploadFile.close();
	
	return fileBytes;
}

/* handle_request() function is to handle upload or download request of the client*/
public static void handle_request (String request, 
		DataInputStream dis, DataOutputStream dos) throws Exception {
	
	String[] request_parts = request.split(" ");
	
	if (request_parts[0].equals("get")) {
			
		dos.writeUTF(request);
		dos.flush();
		
		String status = dis.readUTF();
		
		if(status.equals("File exists")) {
				
			byte[] fileBytes = new byte[dis.readInt()];
			dis.readFully(fileBytes);
			downloadFile(request_parts[1], fileBytes);
			
			System.out.println("File Downloaded Successfully");
		}
		
		else {
			System.out.println(status);
		}
	}
	
	else if (request_parts[0].equals("put")) {
		
		if(isFileExist(request_parts[1])) {
			
			dos.writeUTF(request);
			dos.flush();

			byte[] fileBytes = uploadFile(request_parts[1]);
			
			dos.writeInt(fileBytes.length);
			dos.write(fileBytes);
			dos.flush();
			
			System.out.println("File Uploaded Successfully");
			
		}
		else {
			System.out.println("File doesn't exists. Please enter other file name.");
		}
	}
	
	else {
		dos.writeUTF("Wrong input.\n" + instruction);
		dos.flush();
	}

 }

}
