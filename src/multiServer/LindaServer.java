package multiServer;


import rootClasses.Tuple;
import shm.*;
import java.util.ArrayList;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;



public class LindaServer extends UnicastRemoteObject implements LindaRMI {

   private static final long serialVersionUID = 1L;

   public CentralizedLinda Cl; 
   public ArrayList<String> ListeURI;
   public ArrayList<LindaRMI> ListeServer;
   public ArrayList<Attente> ListeAttente;
   public String URI ;

   public LindaServer(String URI) throws RemoteException{
     this.ListeAttente = new ArrayList();
     this.Cl = new CentralizedLinda();
    this.ListeURI=new ArrayList();
     this.URI = URI;
     this.ListeURI=new ArrayList();
   //  this.ListeURI.add(URI);
     this.ListeServer = new ArrayList(9);
   }                                    
                                      
   public synchronized void write_aux(Tuple tuple) throws RemoteException{
           this.Cl.write(tuple);
           this.notifyAll();
   }
                                   
   public synchronized void write(Tuple tuple) throws RemoteException{
        boolean trouve=false;
       Attente res = null;

        for (Attente a : ListeAttente){
              if (a.motif.matches(tuple)){
                  trouve = true;res=a ;
              }
        }
        if (trouve){
           this.ListeAttente.remove(res);

           for(LindaRMI s : this.ListeServer){
                    s.Actualiser(false,res);
                    if(s.getURI().compareTo(res.serveur)==0){
                    s.write_aux(tuple);
                  }
           }
        }else{this.Cl.write(tuple);}


   } 

   public String getURI() throws RemoteException{return URI;}

   public synchronized void Actualiser(boolean bool,Attente a){
        
      if(bool==true)
         {

              this.ListeAttente.add(a);
         }
      else
         {
            int i = this.ListeAttente.indexOf(a);
             this.ListeAttente.remove(i);
          }
    }

   public synchronized Tuple read(Tuple template) throws RemoteException{
         Tuple t = null;
         t = this.Cl.tryRead(template);
         if (t==null){ 
                            for(LindaRMI s : this.ListeServer){
                                 if (t==null){
                                         t = s.tryRead_aux(template);                                
                                  }
                            }
         }
         if (t==null){
            Attente a = new Attente(template,this.getURI());
            for(LindaRMI s : this.ListeServer){
                s.Actualiser(true,a);
            }
            try{
                while (t==null){
                    this.wait();
                    t =this.Cl.tryRead(template);
                }
             }catch(InterruptedException e){e.printStackTrace();}
         }        
   return t;
   }

   public synchronized Tuple tryTake_aux(Tuple Motif) throws RemoteException{
            return this.Cl.tryTake(Motif);
     }
 
    
   public synchronized Tuple tryRead_aux(Tuple Motif) throws RemoteException{
        return this.Cl.tryRead(Motif);
   }

   public synchronized Tuple take(Tuple template) throws RemoteException{
      Tuple t = null;
             t = this.Cl.tryTake(template);
             if (t==null){ 
                            for(LindaRMI s : this.ListeServer){
                                 if (t==null){
                                         t = s.tryTake_aux(template); 
                                  }
                                }
                          }
              if (t==null){
                            Attente a = new Attente(template,this.getURI());
                            for(LindaRMI s : this.ListeServer){
                                   if(s.getURI().compareTo(this.getURI())!=0)
                                     s.Actualiser(true,a);
                                     }
                            try{
                            while (t==null){
                              this.wait();
                              t =this.Cl.tryTake(template);
                            }
                            }catch (InterruptedException e){e.printStackTrace();}
                         }        
     return t;  
   }


   public synchronized Tuple tryRead(Tuple template) throws RemoteException{
      Tuple t = null;
      boolean trouve = false;
      for(LindaRMI s : this.ListeServer){
             System.out.println("              - "+s.getURI()); }
      for(LindaRMI s : this.ListeServer){
         if(trouve == false)
              {
                 t =   s.tryRead_aux(template);
              }
        if(t!=null) 
            { 
              trouve = true;
           }
        
      }
   return t;
   }

   public synchronized Tuple tryTake(Tuple template) throws RemoteException{
      Tuple t = null;
      boolean trouve = false;
      for(LindaRMI s : this.ListeServer){
         if(trouve == false)
              {
                 t =  this.Cl.tryTake(template);
              }
        if(t!=null) 
            { 
              trouve = true;
           }
      }
   return t;
   }
  


   public synchronized void debug(String prefix){
      System.out.println(prefix);
   }


   public ArrayList<LindaRMI> getListeServer() throws RemoteException{
       return this.ListeServer;
   }
      public synchronized void addListeServer(LindaRMI server) throws RemoteException{
           ListeServer.add(server);
   }

   public synchronized void ActualiserListeServer(String uri,int  PORT,LindaRMI toAdd)throws RemoteException{
             try {System.out.println(URI+" ajoute le serveur : "+uri);
             LindaRMI serv = (LindaRMI)Naming.lookup("rmi://localhost:"+PORT+"/"+uri);
             ListeServer.add(toAdd);}                    
             catch(Exception e){System.out.println("registre déja créé");}

  }
		
        
   public static void main(String args[]){
   
         
      try{
         System.out.println("Enregistrement du serveur : " + args[0]);
         LindaServer serveur = new LindaServer(args[0]); 
         int PORT=4000;

         try{
         LocateRegistry.createRegistry(PORT);
         }catch(Exception e){System.out.println("registre déja créé");}
         Naming.rebind(args[0],serveur);
      System.out.println("Probleme 2");

        Registry registre = LocateRegistry.getRegistry(PORT);
        String[] tab = new String[20];
        for (int i=0;i<registre.list().length;i++){
            tab[i] = registre.list()[i];
         }

         int i ;
          for(i = 0;i<registre.list().length;i++)
           {
              serveur.ListeURI.add(tab[i]);
           }
          for (String uri : serveur.ListeURI){           

                       LindaRMI serv = (LindaRMI)Naming.lookup("rmi://localhost:"+PORT+"/"+uri);

                    if(0!=args[0].compareTo("rmi://localhost:"+PORT+"/"+uri))
                    {
                                   serv.ActualiserListeServer(uri,PORT,serveur);
                                   System.out.println("taille "+serv.getURI()+" : "+serv.getListeServer().size());
                                   serveur.addListeServer(serv);  
                                   System.out.println("Taille "+args[0]+ " :  "+ serv.getListeServer().size());

                     
                     }
               
                   }         
      }catch (Exception e){e.printStackTrace();} 
   }
}
