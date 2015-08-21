package multiServer;

import rootClasses.Callback;
import rootClasses.Linda;
import rootClasses.Tuple;
import java.util.Collection;
import java.rmi.*;


/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
	
    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "//localhost:4000/LindaServer".
     */
    public String URI;
    public LindaRMI serveur;

    public LindaClient(String serverURI) {
        this.URI = serverURI;
        try{
        //   System.out.println("Je suis ici");
           LindaRMI serv = (LindaRMI)Naming.lookup(serverURI);
       //    System.out.println("Je suis la");
           this.serveur = serv;
        }catch (Exception e){e.printStackTrace();}
    }

    /** Adds a tuple t to the tuplespace. */
    public void write(Tuple t){
      try{
      this.serveur.write(t);
      }catch (java.rmi.RemoteException e){e.printStackTrace();}
    }
	
    /** Returns a tuple matching the template and removes it from the tuplespace.
    * Blocks if no corresponding tuple is found. */
    public Tuple take(Tuple template){
       Tuple t = new Tuple();
       try{
       t = this.serveur.take(template);
       }catch (java.rmi.RemoteException e){e.printStackTrace();}
       System.out.println(t);
       return t;
    }
	
    /** Returns a tuple matching the template and leaves it in the tuplespace.
    * Blocks if no corresponding tuple is found. */
    public Tuple read(Tuple template){
      Tuple t = new Tuple();
      try{
      t = this.serveur.read(template);
      }catch (java.rmi.RemoteException e){e.printStackTrace();}
      return t;

    }

    /** Returns a tuple matching the template and removes it from the tuplespace.
    * Returns null if none found. */
    public Tuple tryTake(Tuple template) {
      Tuple res=null ;
     try{
           res= serveur.tryTake(template);
         }
          catch(java.rmi.RemoteException e){e.printStackTrace();}
       return res;
         }

    /** Returns a tuple matching the template and leaves it in the tuplespace.
    * Returns null if none found. */
    public Tuple tryRead(Tuple template){
     Tuple res= null;
     try{
           res= serveur.tryRead(template);
         }
          catch(java.rmi.RemoteException e){e.printStackTrace();}
       return res;}

       

    /** Returns all the tuples matching the template and removes them from the tuplespace.
    * Returns an empty collection if none found (never blocks).
    * Note: there is no atomicity or consistency constraints between takeAll and other methods;
    * for instance two concurrent takeAll with similar templates may split the tuples between the two results. 
    */
    public Collection<Tuple> takeAll(Tuple template){return null;}
    
    /** Returns all the tuples matching the template and leaves them in the tuplespace.
    * Returns an empty collection if none found (never blocks).
    * Note: there is no atomicity or consistency constraints between readAll and other methods;
    * for instance (write([1]);write([2])) || readAll([?Integer]) may return only [2].
    */
    public Collection<Tuple> readAll(Tuple template){return null;}
	
	/** Registers a callback which will be called when a tuple matching the template appears.
	 * The found tuple is removed from the tuplespace.
	 * The callback is kept if it returns true, and is deregistered if it returns false. This is the only way to deregister a callback.
	 * Note that the callback may immediately fire if a matching tuple is already present. And as long as it returns true, it immediately fires multiple times.
	 * Beware: as the firing must wait for the return value of the callback, the callback must never block (see {@link AsynchronousCallback} class). 
	 * Callbacks are not ordered: if more than one may be fired, the chosen one is arbitrary.
	 * 
	 * @param template the filtering template.
	 * @param callback the callback to call if a matching tuple appears.
	 */
	public void eventRegister(Tuple template, Callback callback){}	
    
 
      public void debug(String prefix){
         try{
         this.serveur.debug(prefix);
         }catch(Exception e){e.printStackTrace();}
      }

        
 

}
