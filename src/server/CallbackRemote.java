package server;

import rootClasses.*;
import java.rmi.Remote;




public interface CallbackRemote extends Remote{

    public boolean call(Tuple t) throws java.rmi.RemoteException;
}
