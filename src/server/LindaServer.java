package server;

import rootClasses.*;
import shm.*;
import java.util.Collection;
//import java.util.concurrent.atomic.AtomicInteger;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;


public class LindaServer extends UnicastRemoteObject implements LindaRMI {

   private static final long serialVersionUID = 1L;


   public CentralizedLinda Cl; 
    
  
   public LindaServer() throws RemoteException{
     CentralizedLinda Espace = new CentralizedLinda();
     this.Cl = Espace;
   }                                    
                               
   public void write(Tuple tuple) throws RemoteException{
     this.Cl.write(tuple);
   } 

   public Tuple read(Tuple template) throws RemoteException{
        Tuple t = this.Cl.read(template);
        return t;
     }


   public Tuple take(Tuple template) throws RemoteException{
      Tuple t = this.Cl.take(template);
      return t;
   }

   public Tuple tryTake(Tuple template) throws RemoteException{
      Tuple t =this.Cl.tryTake(template);
      return t;
   }

   public Tuple tryRead(Tuple template) throws RemoteException{
      Tuple t = this.Cl.tryRead(template);
      return t;
   }

   public Collection<Tuple> readAll(Tuple tuple) throws RemoteException{
      Collection<Tuple> l = this.Cl.readAll(tuple);
      return l;
   }

   public Collection<Tuple> takeAll(Tuple tuple) throws RemoteException{
      Collection<Tuple> l = this.Cl.takeAll(tuple);
      return l;
   }

   public void eventRegister(Tuple t,CallbackRemote cb) throws RemoteException{
      Callback callbackServer = new CallbackServeur(cb);
      this.Cl.eventRegister(t,callbackServer);
   }


  
   public void debug(String prefix){
      System.out.println(prefix);
   }



   public static void main(String args[]){
      final int PORT=4000; 
      String URI;
      try{
         //Creation du registre
         System.out.println("création du registre ");
         try{
         LocateRegistry.createRegistry(PORT);
         }catch (Exception e){System.out.println("registre déjà créée");}
         //Création du Serveur
         LindaServer Serveur = new LindaServer();
         //Calcul de l'URL des deux serveurs
         URI = "//localhost:"+PORT+"/MonServeur";
         //Enregistrement dans l'annuaire des deux serveurs
         Naming.rebind(URI,Serveur);	 
         System.out.println("Enregistrement du serveur avec l'url : " + URI);
      }catch (Exception e){}
   }

}
