
package test;

import multiServer.*;
import rootClasses.*;
import server.*;
import shm.*;

public class BasicTestAsyncCallback {

    private static class MyCallback implements Callback {
        public boolean call(Tuple t) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("Got "+t);
            return true;
        }
    }
	
    public static void main(String[] a) {
        //Linda linda = new linda.shm.CentralizedLinda();
         Linda linda = new server.LindaClient("//localhost:4000/MonServeur");
			
        Tuple motif = new Tuple(Integer.class, String.class);
        linda.eventRegister(motif, new AsynchronousCallback(new MyCallback()));
		
        Tuple t1 = new Tuple(4, 5);
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        Tuple t3 = new Tuple(4, "foo");
        System.out.println("(2) write: " + t3);
        linda.write(t3);
					
        linda.debug("(2)");

    }

}
