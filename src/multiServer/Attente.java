package multiServer;

import rootClasses.*;



public class Attente implements AttenteInt{


   public Tuple motif;
   public String serveur;
   //public boolean read; // renvoie true si le thread qui s'est bloqué a effectué un read
   
   public Attente(Tuple m,String s){
      this.motif = m;
      this.serveur = s;
   }

}
