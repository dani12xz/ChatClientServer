
package com.mycompany.clientchat1;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static java.lang.System.out;



public class  ChatClient extends JFrame implements ActionListener {
    
    //VARIABILI
    String uname;
    PrintWriter pw;
    BufferedReader br;
    JTextArea  taMessages;
    JTextField tfInput;
    JButton btnSend,btnExit,btnList,btnChat;
    Socket client;
    String tmpName;
    ArrayList<String> users =new ArrayList<String>();
    
    
    
    //COSTRUTTORE
    public ChatClient(String uname,String servername) throws Exception {
        super(uname);  // titolo per il frame 
        this.uname = uname;
        client  = new Socket(servername,9999);
        br = new BufferedReader( new InputStreamReader( client.getInputStream()) ) ;
        pw = new PrintWriter(client.getOutputStream(),true);
        pw.println(uname);  // invio nome al server
        buildInterface();
        new MessagesThread().start();  // thread per ricevere i messaggi
        users.add("All");//default
    }
    
    //INTERFACIA
    public void buildInterface() {
        btnSend = new JButton("Invia");
        btnExit = new JButton("Esci");
        btnList = new JButton("List");
        btnChat = new JButton("Chat");
        taMessages = new JTextArea();
        taMessages.setRows(10);
        taMessages.setColumns(50);
        taMessages.setEditable(false);
        tfInput  = new JTextField(50);
        JScrollPane sp = new JScrollPane(taMessages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp,"Center");
        JPanel bp = new JPanel( new FlowLayout());
        bp.add(tfInput);
        bp.add(btnSend);
        bp.add(btnExit);
        bp.add(btnList);
        bp.add(btnChat);
        add(bp,"South");
        btnSend.addActionListener(this);
        btnExit.addActionListener(this);
        btnList.addActionListener(this);
        btnChat.addActionListener(this);
        setSize(500,300);
        setVisible(true);
        pack();
    }
    
    
    // eventi dei bottoni
    public void actionPerformed(ActionEvent evt) {
        String tmp;
        if ( evt.getSource() == btnExit ) {
            pw.println("/Fine");  // invia FINE al server per terminare 
            System.exit(0);
            
        }
        else if(evt.getSource() == btnChat)
        {

            
            //crea intefacia con elenco dei partecipanti
            tmpName =(String) JOptionPane.showInputDialog(null,"Scegli la modalita della chat","Chat",JOptionPane.QUESTION_MESSAGE,null, users.toArray(), users.get(0));
            pw.println("/Chat "+tmpName);//avvia la modalita inserita

                        
        }
        else if(evt.getSource() == btnList)
        {
            pw.println("/List");
        }
        else 
        {//PER INVIARE MESSAGGI AL SERVER
            
            pw.println(tfInput.getText());
            
            tfInput.setText("");//per svuotare la barra di input
            
        }
    }
    
    
    public static void main(String[] args) {
    
        //INSERIMENTO NOME
        String name;
       
        String info="INFO:Inserisci il nome";
        do{
             name = JOptionPane.showInputDialog(null,"Inserisci il nome :", info,JOptionPane.PLAIN_MESSAGE);
             
             if(name==null)
             {
                 System.exit(0);
             }
  
        }while(name==null);
        
        
        String servername = "localhost";  
        try {
            new ChatClient( name ,servername);
        } catch(Exception ex) {
            out.println( "Errore --> " + ex.getMessage());
        }
        
    } // fine main
    
    
    class  MessagesThread extends Thread {
        public void run() {
            String line;
            pw.println("/HideList");
            
            
            try {
                while(true) {
                    
                    line = br.readLine();//stringa ricevuta
                    String[] part = line.split("-");//separa la frase
                    if(part[0].equals("hide")){//controllo se la 1 parte e = a hide
                        
                        if(!part[1].equals("solo") && part[2].equals("add"))//controllo se e diverso da solo
                        {
                            
                            users.add(part[1]);
                        }
                        if(!part[1].equals("solo") && part[2].equals("remove"))//controllo se e diverso da solo
                        {
                            
                            users.remove(part[1]);
                        }
                        
                        
                    }
                    else
                    {
                        taMessages.append(line + "\n");//output in chat
                    }
                    
                    
                    
                } // fine while
            } catch(Exception ex) {}
        }
    }
} //  fine client
