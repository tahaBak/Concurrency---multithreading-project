package test;

import multiServer.*;
import rootClasses.*;
import server.*;
import shm.*;
public class BasicTest3 {

    public static void main(String[] a) { 
        //final int PORT=4000;
        final Linda linda = new shm.CentralizedLinda();
        //final Linda linda = new linda.server.LindaClient("//localhost:"+PORT+"/MonServeur");	
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                Tuple motif = new Tuple(Integer.class, String.class, "5");
                Tuple res = linda.tryTake(motif);
                System.out.println("(1) Resultat:" + res);

                Tuple motif1= new Tuple(4, 4);
                Tuple res1 = linda.tryTake(motif1);
                System.out.println("(1) Resultat:" + res1 );
             

                Tuple motif2= new Tuple(Boolean.class, Integer.class);                
                Tuple res2 = linda.take(motif1);
                System.out.println("(1) Resultat :" + res2);
                
                linda.debug("(1)");
             
            }
        }.start();
		
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t1 = new Tuple(4, 5);
                System.out.println("(2) write: " + t1);
                linda.write(t1);
          
                Tuple t11 = new Tuple(4, 5);
                System.out.println("(2) write: " + t11);
                linda.write(t11);

                Tuple t2 = new Tuple("hello", 15);
                System.out.println("(2) write: " + t2);
                linda.write(t2);

                Tuple t3 = new Tuple(4, 4);
                System.out.println("(2) write: " + t3);
                linda.write(t3);
            
                linda.debug("(2)");

            }
        }.start();
		
    }
}
