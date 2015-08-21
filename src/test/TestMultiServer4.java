package test;

import multiServer.*;
import rootClasses.*;
import server.*;
import shm.*;
import java.util.ArrayList;
import java.util.Collection;

public class TestMultiServer4 {

    public static void main(String[] a) { 
        final int PORT=2029;
        //final Linda linda = new linda.shm.CentralizedLinda();
        final Linda linda1 = new multiServer.LindaClient("rmi://localhost:"+PORT+"/Serveur1");
        final Linda linda2 = new multiServer.LindaClient("rmi://localhost:"+PORT+"/Serveur2");

	//System.out.println("Probleme");	
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                Tuple motif = new Tuple(Integer.class, Integer.class);
       /*         Collection<Tuple> res = linda.takeAll(motif);
                int indice = 0;
                for (Tuple t: res){
                    System.out.println("(1) Resultat numero "+ indice + " = " + t);
                    indice++;
                }*/
                Tuple motif1= new Tuple(Boolean.class, Integer.class);
                Tuple res1 = linda1.tryRead(motif1);
                System.out.println("(1) Resultat devrait doit ne pas etre null:" + res1);
                Tuple motif2= new Tuple(Integer.class, Integer.class);
                Tuple res2 = linda2.tryRead(motif2);
                System.out.println("(1) Resultat devrait ne pas  etre null:" + res2);
          
               Tuple motif3= new Tuple(Integer.class, String.class,String.class);
                                       Tuple res3 = linda1.read(motif3);
       System.out.println("(3) Resultat devrait ne pas  etre null :" + res3);


                //On essaye de lire un motif qu'on a déja lu pour vérifier que le tryRead ne le supprime pas de l'espace de tuple
                
                

                linda1.debug("(1)");
                           }
        }.start();
		
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t1 = new Tuple(4, 5);
                System.out.println("(2) write: " + t1+" sur le serveur1");
                linda1.write(t1);
          
                Tuple t11 = new Tuple(4, 6);
                System.out.println("(2) write: " + t11+ " sur le serveur2");
                linda2.write(t11);

                Tuple t2 = new Tuple("hello", 15);
                System.out.println("(2) write: " + t2 + " sur le serveur 1");
                linda1.write(t2);
 
           /*     System.out.println("on va attendre 2 secondes: " );
                            try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
 
                
                Tuple t3 = new Tuple(4, "debloquage","serveur2");
                 linda2.write(t3);

                System.out.println("(2) write: " + t3+ " sur le serveur 2");
   
            
                linda2.debug("(2)");

            }
        }.start();
		
    }
}
