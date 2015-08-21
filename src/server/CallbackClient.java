package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import rootClasses.*;

public class CallbackClient extends UnicastRemoteObject implements CallbackRemote {

private static final long serialVersionUID = 1L;
	private Callback callback;
	
	public CallbackClient(Callback callback) throws RemoteException {
		this.callback = callback;
	}
		
	public boolean call(Tuple t) throws RemoteException {
		boolean forward = callback.call(t);
		return forward;
	}

}
