import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/* A ClientInstances class to handle multiple client request concurrently on the server*/

public class ClientInstances extends Thread {

    private Thread thread;
    Socket socket;

    public ClientInstances(Socket socket){
        this.socket = socket;
    }

    public void start(){
        if(thread == null){
            this.thread = new Thread(this);
        }
        thread.start();
    }

    public void run(){
        try{
        
            System.out.println("Client request from "+socket.getInetAddress()+":"+socket.getPort()+" connected successfully");
		
		    DataInputStream dis = new DataInputStream(socket.getInputStream());
		    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		
		    dos.writeUTF(Server.message+Server.instruction);
		    dos.flush();
		
		    String request="";
		    while(true)
            {
                request = dis.readUTF();
                
                if(request.equals("quit"))
                {
                    System.out.println("Client from "+socket.getInetAddress()+":"+socket.getPort()+" has Disconnected");
                    dos.writeUTF("Good bye Client. Have a nice day !");
                    dos.flush();
                    break;
                }
                
                Server.handle_request(request, dis, dos);
            }
            dis.close();
            dos.close();
            socket.close();
        }
        catch(Exception e){
            System.out.println("Client from "+socket.getInetAddress()+":"+socket.getPort()+" has Disconnected");
        }
    }

}