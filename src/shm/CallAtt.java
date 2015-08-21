package shm;
import rootClasses.*;




public class CallAtt{

  public Callback eva ;
  public boolean activer ;
  public Tuple motif;
  
  public CallAtt(Callback eva,Tuple motif){
     this.eva =eva;
     this.activer = true;
     this.motif=motif ;
     }
  public void desactiver(){
     this.activer = false;
     }
}
