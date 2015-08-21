package server;

import rootClasses.*;

/** Permet d'utiliser le CallbackClient (qui est Remote) dans l'event register du CentralizedLinda
 * C'est lui qu'on envoit dans CentralizedLinda
 * Son call lance le call du CallbackClient (qui lui-meme lance le call du Callback initial)
 */
public class CallbackServeur implements Callback {

	CallbackRemote callbackClient;
	
	public CallbackServeur(CallbackRemote callbackClient) {
		this.callbackClient = callbackClient;
	}
	
	public boolean call(Tuple t) {
		boolean forward = false;
		try {
			forward = callbackClient.call(t);
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		return forward;
	}
}
