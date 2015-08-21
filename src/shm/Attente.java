package shm;
//import linda.Callback;
import rootClasses.*;




public class Attente{

  public Tuple motif ; 
  public Object Verrou ;
  public int nbReaders;
  public int nbTakers ;
  public  int nbPassed ;
  public boolean Notifie ;
  
  public Attente(Tuple Motif){
     this.motif=Motif;
     this.Verrou = new Object();
     this.nbReaders = 0;
     this.nbTakers = 0;
      Notifie=true ;

      }

    public int addReader(){
       return nbPassed=(nbPassed+1)%3;
    }
   
   // public void Signaler(){
     //  if(nbReaders>0){
       //this.nbReaders--;
       //ReadersVerrou.notify();
    //}else{
       //this.nbTakers--;
       //Verrou.notify();
    // }
    public int nbAttente(){
       return nbReaders+nbTakers ;
    }
 }          
