package test;

import multiServer.*;
import rootClasses.*;
import server.*;
import shm.*;
import java.util.ArrayList;
import java.util.Collection;

public class TestMultiServer3 {

    public static void main(String[] a) { 
        final int PORT=2019;
        //final Linda linda = new linda.shm.CentralizedLinda();
        final Linda linda1 = new multiServer.LindaClient("rmi://localhost:"+PORT+"/serv1");
        final Linda linda2 = new multiServer.LindaClient("rmi://localhost:"+PORT+"/serv2");

	//System.out.println("Probleme");	
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                Tuple motif = new Tuple(Integer.class, Integer.class);
          
               Tuple motif3= new Tuple(Integer.class, String.class,String.class);
               Tuple res3 = linda1.tryRead(motif3);
               System.out.println("(3) Resultat devrait ne pas  etre null :" + res3);


                //On essaye de lire un motif qu'on a déja lu pour vérifier que le tryRead ne le supprime pas de l'espace de tuple
                
                

                linda1.debug("(1)");
                           }
        }.start();
		
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t1 = new Tuple(4, 5);
                System.out.println("(2) write: " + t1+" sur le serveur1");
                linda1.write(t1);
          
                Tuple t11 = new Tuple(4, 6);
                System.out.println("(2) write: " + t11+ " sur le serveur 2");
                linda2.write(t11);

                Tuple t2 = new Tuple("hello", 15);
                System.out.println("(2) write: " + t2 + " sur le serveur 1");
                linda1.write(t2);
 
                
                Tuple t3 = new Tuple(4, "debloquage","serveur2");
                 linda2.write(t3);

                System.out.println("(2) write: " + t3+ " sur le serveur 2");
   
            
                linda2.debug("(2)");

            }
        }.start();
		
    }
}
