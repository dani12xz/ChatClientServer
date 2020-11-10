
package com.mycompany.serverchatt;


import java.io.*;
import java.util.*;
import java.net.*;
import static java.lang.System.out;

public class  ChatServer {
    //vetori
    Vector<String> users = new Vector<String>();
    Vector<HandleClient> clients = new Vector<HandleClient>();
    int maxClient = 100;//numero massimo di client
  
    //avvio
    public void comunica() throws Exception  {
      
        HandleClient ctmp;
        ServerSocket server = new ServerSocket(9999);//porta
      
        out.println("Server partito...");
        while(true) {
            if(clients.size()<maxClient)
            {
                Socket client = server.accept();
 		HandleClient c = new HandleClient(client);
  		clients.add(c);
                joinUser(c.getUserName());
            }
            
         
 		 
                 
        }  //fine del while
    }
  
    //MAIN
    public static void main(String[] args) throws Exception {
        new ChatServer().comunica();
    } // fine del main
  
  
    public void broadcast(String user, String message)  {
	    //invia a tutti quelle connessi
            
	    for ( HandleClient c : clients ){
	       if ( ! c.getUserName().equals(user) )c.sendMessage(user,message);
               else
               c.sendMessage("io", message);
            }
    }
    public void joinUser(String user)  {
	    //invia a tutti quelle connessi
            
	    for ( HandleClient c : clients ){
	       if ( ! c.getUserName().equals(user) ){
                   c.output.println(user+" è entrato nella chat");
                   c.output.println("hide-"+user+"-add");
                   
               }
               else
               c.output.println(user+ " sei entrato nella chat");
            }
    }
  
  
  
  class  HandleClient extends Thread {
      
      //VARIABILI 
      String name = "";
      BufferedReader input;
      PrintWriter output;
        
      //costruttore
        public HandleClient(Socket  client) throws Exception {
         //input / output
	 input = new BufferedReader( new InputStreamReader( client.getInputStream())) ;
	 output = new PrintWriter ( client.getOutputStream(),true);
	 //legge nome
	 name  = input.readLine();
         if(name.isBlank()==true)name="Utente";
         String tmpName=name;
         for(int i=0;i<users.size();i++)//controllo nome
         {
             String[] part2 = users.get(i).split("#");
             if(name.equals(part2[0]))
            {

                
                if(part2.length == 1)
                {
                    tmpName=name+"#1";
                }
                else
                {
                    
                    int tmp = Integer.parseInt(part2[1]);
                    tmpName=name+"#"+(tmp+1);
                    
                }
                
                
                
            }
         }
         
         name= tmpName;
         
         
	 users.add(name); //aggiunge al vetore
         
	 start();
        }
        
        public void sendMessage(String uname,String  msg)  {
	    output.println( uname + ":" + msg);
	}
        public void sendComandsList()  {
	    output.println("/Fine : Per chiudere il collegamento"+"\n"+"/List : Per avere la lista di tutti i partecipanti "+"\n"+ "/Chat nomeUtente : Per scrivere solo a 1 solo persona "+"\n"+"/Chat All : Per scrivere a tutti quanti");
	}
        public void sendPrivateMessage(String uname1 ,String uname2,String line)  {//utente1  =da chi viene , utente 2 = da chi va , line = messaggio
	    for ( HandleClient c : clients ){
	       if ( c.getUserName().equals(uname2) ){
                   c.output.println(uname1+"--->"+uname2 +":"+line);
                   output.println("io"+"--->"+uname2 +":"+line);
               }
            }
	}
        public void sendLogOutMessage()  {
	    for ( HandleClient c : clients ){
	       if ( ! c.getUserName().equals(name) ){
                   c.output.println(name +", si e scollegato.");
                   c.output.println("hide-"+name +"-remove");
               }
                   
            }
	}
        public void sendUsersList(){
            if(clients.size() == 1)
            {
                output.println("Sei l'unico in questa chat");
            }
            else
            {
                for ( HandleClient c : clients ){
	        if ( ! c.getUserName().equals(name) )output.println(c.getUserName());
                }

            }
            
        }
		
        public String getUserName() {  
            return name; 
        }
        
        public void run(){
    	    String line;
            Boolean modalitaTutti=true; 
            String privatName="";
            int tmp=0;
            output.println("Per avere informazioni scrivere /help");
	    try{
                 
                while(true){//DISCONESSIONE
		    line = input.readLine();
                    tmp=0;
                    String[] part = line.split(" ");
		    if ( line.equals("/Fine") ) {
                   
                        output.println( "Ti sei scollegato, premi ESCI per chiudere la finestra " + "\n");    
                        sendLogOutMessage();
                        clients.remove(this);
                        users.remove(name);
                        break;
                    }
                    else if ( line.equals("/help") ) {
                        sendMessage("io", "/help");
                        sendComandsList();

                    }
                    else if ( line.equals("/List") ) {
                        sendMessage("io", "/List");
                        sendUsersList();
                    }
                    else if ( line.equals("/HideList") ) {
                        if(clients.size() == 1)
                        {
                            //output.println("/hide-solo-add");
                        }
                        else
                        {
                            for ( HandleClient c : clients ){
                            if ( ! c.getUserName().equals(name) )output.println("hide-"+c.getUserName()+"-add");
                            }

                        }
            
                    }
                    else if(part[0].equals("/Chat"))//CONTROLLO SE E IL COMANDO /CHAT
                    {
                        if(part[1].equals("All")){
                            
                            modalitaTutti=true;
                            sendMessage("io", "/Chat All");
                                
                            output.println("Chat Modalita Tutti");
                        }
                        else
                        {
                                
                                
                               
                            for ( HandleClient c : clients ){
                                    
                                if ( c.getUserName().equals(part[1]))//controllo che esista e che non sia lui
                                {
                                        
                                    privatName=c.getUserName();//SALVA IL NOME
                                    modalitaTutti=false;
                                    output.println("Chat Modalita Singola");
                                    sendMessage("io", "/Chat "+privatName);
                                    break;
                                }
                                else//non esiste
                                {
                                    tmp++;
                                        
                                }
                            }
                            if(tmp==clients.size())
                            {
                                output.println("utente non e presente nella chat");
                            }
                               
                        }
                    }
                    else
                    {
                        //DEFAULT
                        
                        if(modalitaTutti==true)broadcast(name,line); // per mandare il messaggio a tutti 
                        else{
                            sendPrivateMessage(name, privatName, line);
                        }
                            
                    }
                    
		   
	       } // fine while
                
	    } // fine try
	    catch(Exception ex) {
	        System.out.println(ex.getMessage());
	    }
        } // end of run()
    } // end of inner class
} // end of Server
