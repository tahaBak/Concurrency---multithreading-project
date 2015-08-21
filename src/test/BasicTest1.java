package test;

import rootClasses.*;

public class BasicTest1 {

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
                
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda.take(motif);
                System.out.println("(1) Resultat:" + res);

                Tuple motif1= new Tuple(Integer.class, Integer.class);   
                Tuple res1 = linda.take(motif1);
                System.out.println("(2) Resultat:" + res1);
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

                Tuple t3 = new Tuple(4, "foo");
                System.out.println("(2) write: " + t3);
                linda.write(t3);
            
                linda.debug("(2)");

            }
        }.start();
		
    }
}
