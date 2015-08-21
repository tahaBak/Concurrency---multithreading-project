package shm;

import rootClasses.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/** Shared memory implementation of Linda. */
/*Priorité à ceux qui effectuent un Read*/
public class CentralizedLinda implements Linda {
	
     /** To debug, prints any information it wants (e.g. the tuples in tuplespace or the registered callbacks), prefixed by <code>prefix</code. */
     private ArrayList<Tuple> EspaceTuple;  
     private ArrayList<Attente> Demander;
     private ArrayList<Attente> TupleAttente;
     private ArrayList<Object>  Verrous;
     //private Semaphore ticket;
     private AtomicInteger index;
     private ArrayList<CallAtt> LesCalls;
    // private Lock moni;

     public CentralizedLinda(){
        Tuple motif1=new Tuple(4,4);
        Tuple motif2=new Tuple(Boolean.class, Integer.class);
        this.EspaceTuple = new ArrayList(100);
        this.EspaceTuple.add(motif1);
        this.EspaceTuple.add(motif2);
        this.TupleAttente = new ArrayList(100);
        this.Verrous = new ArrayList(30);
        for (int i=0;i<30;i++){
           Object o = new Object();
           this.Verrous.add(o);
        }
        LesCalls = new ArrayList(0);

        this.index = new AtomicInteger();
     }


    public boolean write_aux(Tuple tuple,int i,int n)throws InterruptedException{
                       /*comme ça on aura tous les verrous*/
       boolean res = false;
        if(i<30){
          synchronized(this.Verrous.get(i)){
             res = write_aux(tuple,i+1,0);
            this.Verrous.get(i).notifyAll();
          }
       }
       else{
       //cette partie est pour les callsbacks
            boolean consomme=false ;
            int indice= 0;
            CallAtt  t ;
            int  size = LesCalls.size();  // la taille de l'espace de tuples
              while((indice<size)&&(consomme == false)){
                  t = LesCalls.get(indice); // le i eme element
                   if (tuple.matches(t.motif) && t.activer){     // on verifie si le tuple a ecrire corresponds au motif et que le callback est activer
                             consomme=true;  
                             t.activer=t.eva.call(tuple);   
                             this.EspaceTuple.remove(indice); 
                                          
                   }
                   else{
                       indice++;
                        }
                }
            ///
             if(!consomme)  //on fait rien s'il a ete consommé par un callback
                {          //
                    EspaceTuple.add(tuple);
                  
                }
               res = consomme ;
            }
          return res ;
 }

    public void write(Tuple tuple){
       try{
          boolean used = write_aux(tuple,0,0) ;
          if(!used){              // on fait ça si le tuple est bien deposé dans EspaceTuple
                    Attente res = null;
                    boolean trouve = false;
                //    System.out.println("maintenant on cherche les objets qui attendent") ;
                    for(Attente t: this.TupleAttente){
                 //        System.out.println("objet en attente : "+t.motif);
                         if(tuple.matches(t.motif)){
                   //          System.out.println("un motif trouve");
                             res = t; trouve = true;
                               }
                     } 
                    if (trouve && (res.nbTakers>0||res.nbReaders>0)){
                 //                System.out.println("on notifie " + res.motif);
                                 synchronized(res.Verrou){
                                      res.Verrou.notifyAll();
                                  }
                   }
          }
        }
          catch (InterruptedException e){e.printStackTrace();}
    }
 /***************************************************************************************************************/

     public Tuple read(Tuple template){
       /********************************************************************************
            prends un tickets pour limiter le nombre de threads qui travaillent en parallele 
       **********************************************************************************/
        //30 lectures possibles au maximum (arbitraire)
        int indice = index.getAndIncrement()%30;
        boolean InTupleSpace=false;
        boolean InTupleAttente=false;
        Attente res = null;
        Tuple resultat = null;
        try{
             while(resultat == null) {
                   
              synchronized(Verrous.get(indice)){
       	                  for (Tuple t : EspaceTuple){
               	              	if (t.matches(template)){
              		    	                resultat = t ;
        		                        InTupleSpace=true;
                      		}
                           }
               	    if (!InTupleSpace){ 
                                      
       		         synchronized(TupleAttente){
	     	             for(Attente a:TupleAttente){
                                 if(a.motif.matches(template)){			
       	           /* ajout d'un lecteur de façon atomique*/
    		                 res = a ;
                                 InTupleAttente=true;
           	                 }
         	             }
                	     if(!InTupleAttente){
                  	     Attente attente = new Attente(template);
          	       	     TupleAttente.add(attente) ;
                 	     res = attente ;
                	     }
                	  }
              	      }
          /*plus de raisons pour monopoliser le verrou */
              }
           if(!InTupleSpace){
             synchronized(res.Verrou){
          //On incrémente le nombre de lecteurs en attente sur res
                res.nbReaders++;
           // System.out.println("un lecteur se bloque");     
                res.Verrou.wait();
            //System.out.println("un lecteur se debloque et verifie s'il peut passer");
		while((res.nbTakers>0) && (res.nbPassed>3)){
               //On réveil tout le monde pour éviter un interblocage
              //      System.out.println("c'est pas le bon moment pour se debloquer");
                    res.Verrou.notifyAll();     	       
                    res.Verrou.wait(); 
                }
                 res.nbPassed++;
                 res.nbReaders--;
                 res.Verrou.notifyAll();
                 }            
           }
   	 }
     }catch(InterruptedException e){e.printStackTrace();}
         return resultat;
}

public TupleAtt take_aux(Tuple template,int i)throws InterruptedException{
          /*comme ça on aura tous les verrous*/
          Tuple res = null;
          boolean InTupleAttente = false;
          Attente x = null;
          int size;
          TupleAtt ta = new TupleAtt();
          if(i<30){
              synchronized(this.Verrous.get(i)){
                 ta= take_aux(template,i+1);
              }
          }
          else{
              int indice =  0;
              boolean trouve = false;
              Tuple t ;
              size = EspaceTuple.size();  // la taille de l'espace de tuples 
              while((indice<size)&&(trouve == false)){
              t = EspaceTuple.get(indice); // le i eme element
                   if (t.matches(template)){     //
                   trouve=true;
                   }
                   else{
                       indice++;
                        }
                }
                if (trouve){     // retirer l'element d'indice i-1 a coder
                   res = this.EspaceTuple.remove(indice);
                }
                 if(!trouve){
               //synchronized(TupleAttente){
               for(Attente a:TupleAttente){
                  if(a.motif.matches(template)){			
       	          /* ajout d'un lecteur de façon atomique*/
  		  x = a ;
             	  InTupleAttente=true;
                  }
               }
               if(!InTupleAttente){
                  x= new Attente(template) ;
                  TupleAttente.add(x) ;
               }
              }
              

              ta.tuple = res;
	  ta.attente = x;
	}
 

      //  System.out.println("element  trouve take _aux N "+i+"  : "+ta.tuple); 

          return ta;
}

   public Tuple take(Tuple template){
   Tuple res=null;
   Attente x;
   TupleAtt resultat ;
    

   try{  
      resultat = take_aux(template,0) ;
      res = resultat.tuple ;
      x = resultat.attente ;
      while(res == null){
         synchronized(x.Verrou){                    
            x.nbTakers++ ; //incremente le nombre de takers    
            x.Verrou.wait();  
           
             
            while((x.nbReaders>0)&&(x.nbPassed<3)){
               x.Verrou.notifyAll(); 
               x.Verrou.wait();
            }


            x.nbTakers--;
            x.nbPassed=0;           //on remets le compteur de lectures successifs a 0 ;
            x.Verrou.notifyAll();
            res = take(template);
            
         }  // on lache le verrou  de x               
      }
      //System.out.println("(1) ok2");

     }catch(InterruptedException e){e.printStackTrace();}
      return res;
   }
  
   public void debug(String prefix){
      System.out.println(prefix);
   }


public Tuple tryTake_aux(Tuple motif, int i){
 
          /*comme ça on aura tous les verrous*/
          Tuple res = null;
          //boolean InTupleAttente = false;
          Attente x = null;
          int size;
         // TupleAtt ta = new TupleAtt();
          if(i<30){
              synchronized(this.Verrous.get(i)){
                 res = tryTake_aux(motif,i+1);
              }
          }
          else{
              int indice =  0;
              boolean trouve = false;
              Tuple t ;
              size = EspaceTuple.size();  // la taille de l'espace de tuples
              while((indice<size)&&(trouve == false)){
                        t = EspaceTuple.get(indice); // le i eme element
                        if (t.matches(motif)){     //
                         trouve=true;
                         }
                         else{
                         indice++;
                          }
               }
               if (trouve){     // retirer l'element d'indice i-1 a coder
                     res = this.EspaceTuple.remove(indice);
               }
        }
        return res ;
  }
  
public Tuple tryTake(Tuple motif){
  
     return tryTake_aux(motif,0); 
    
}

public Tuple tryRead(Tuple motif){
        int indice = index.getAndIncrement()%30;
        boolean InTupleSpace=false;
        boolean InTupleAttente=false;
        Attente res = null;
        Tuple resultat = null;
        
        synchronized(Verrous.get(indice)){
                       
                              for (Tuple t : EspaceTuple){
                                 	if (t.matches(motif)){
       	         	                       resultat = t ;
       	                                }              	        	
                                }
         }
         
         return resultat;
 }

public Collection<Tuple> takeAll(Tuple motif){
      ArrayList<Tuple> liste = new ArrayList();
      boolean Trouve = true;
      Tuple res = this.tryTake(motif);
      while(res!=null){
           liste.add(res);
           res = tryTake(motif);
      }
       return liste ;
 }
           
public Collection<Tuple> readAll(Tuple motif){
        int indice = index.getAndIncrement()%30;
        boolean InTupleSpace=false;
        boolean InTupleAttente=false;
       // Attente res = null;
        ArrayList<Tuple> resultat = new ArrayList();
  

        
           synchronized(Verrous.get(indice)){
                    for (Tuple t : EspaceTuple){
                         	if (t.matches(motif)){
       	         	                resultat.add(t);
       	                         }              	
                    }
           }
        
        return resultat ;
 }


public void eventRegister_aux(Tuple motif,Callback call_me,int i){
  
          int size;
         // TupleAtt ta = new TupleAtt();
          if(i<30){
              synchronized(this.Verrous.get(i)){
                   eventRegister_aux(motif,call_me,i+1);
                   }
          }
          else{
              int indice =  0;
              boolean activer = true;
              Tuple t ;
              size = EspaceTuple.size();  // la taille de l'espace de tuples
              while((indice<size) && activer){
                        t = EspaceTuple.get(indice); // le i eme element
                        if (t.matches(motif)){     //
                                  activer = call_me.call(t);
                                  this.EspaceTuple.remove(indice);
                                  }
                        indice++;
                        
                   }
               if(activer){
                  CallAtt element = new CallAtt(call_me, motif);
                  LesCalls.add(element);
               }
           }
 }
 
public void eventRegister(Tuple motif,Callback call_me){

      eventRegister_aux(motif,call_me,0);

}
}

