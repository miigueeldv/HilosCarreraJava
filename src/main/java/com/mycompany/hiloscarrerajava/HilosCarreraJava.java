/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.hiloscarrerajava;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
class Carrera
{
    private int distancia;     //Distanacia que tienen que recorrer los coches
   
    private boolean terminada; //Indicador de si la carrera ha terminado
    
    private boolean terminadaCarrera;
    
    private int vueltas=1;
    
    private int comprobacion=0;
    private HashMap<String,Integer> avances = new HashMap<String,Integer>();  //Para registrar el recorrido de cada coche
    
    private HashMap<String,Integer> total=new HashMap<String,Integer>();
    
    private Random random=new Random();
    
    public Carrera(int distancia, boolean terminado)
    {
       this.distancia=distancia;
       this.terminada=terminado;
       this.terminadaCarrera=false;
   
    } 
    
    public synchronized void AvanzaHilo(String Nombre,int recorrido)
    {
        
       double porcentaje;   //Porcentaje de la distancia recorrida 
       
       if (!this.terminadaCarrera){
            if (vueltas <=3){
                 if (!this.getTerminado())     
                 { 
                    System.out.print("El hilo "+Nombre+" recorre "+recorrido+" metros ");

                    if ( recorrido>=this.distancia)
                    {
                        this.terminada=true;   //La carrera ha terminado
                        System.out.print("HILO-------- "+ Nombre+ " -----HA GANADO!!!!!!!!!!!!!"); 
                    }  
                    else  //Si no ha ganado
                    {
                        porcentaje= Math.round(recorrido*100/this.distancia);  // Calculamos el porcentaje de distancia recorrida
                        System.out.print("HILO-------- "+ Nombre+ " Ha recorrido el "+porcentaje+"% de la distancia");      
                    }    
                   this.avances.put(Nombre,recorrido);//Registramos el recorrido de ese coche 
                 }
                 else{
                     if (comprobacion == 3 ){
                         this.terminada=false;
                         vueltas++;
                         distancia=100+(random.nextInt(400)+1);
                         comprobacion=0;
                         System.out.println("VUELTA "+(vueltas-1)+" terminada. Empieza otra: !! "+this.distancia);
                     }
                     else{
                        int anterior=total.getOrDefault(Nombre, 0);
                        int av=avances.getOrDefault(Nombre, 0);
                        this.total.put(Nombre, av+anterior);
                        comprobacion++;
                     }
                 }
                   System.out.println();   //Saltamos la linea
            }
            else{
                this.terminadaCarrera=true;
                System.out.println("HA TERMINADO LA CARRERA");
            }
       }
          
    }        
    
    public synchronized boolean getTerminado()     //Metodo para comprobado si la carrera ha terminado
    {
       return this.terminada;
    
    } 
    
    public HashMap getAvances()    //Retornamos el HashMap con los avances
    {
       return this.avances;
    }

    public HashMap<String, Integer> getTotal() {
        return total;
    }

    public synchronized int getVueltas() {
        return vueltas;
    }

    public synchronized boolean isTerminadaCarrera() {
        return terminadaCarrera;
    }
    
}        

class Coche implements Runnable  
{
  private String Nombre;  //Nombre del hilos
  private int avanza;     //Metros que avanza en cada iteración
  private int recorrido=0;  //Distancia que ese hilo lleva recorrido   
  private Random ran=new Random();
  private final Carrera carr;  //Instacia de la carrera que le pasamos desde el principal
  
  public Coche(String Nombre,Carrera carr)
  {
      this.Nombre=Nombre;
      this.avanza=0;
      this.carr=carr;
  
  }        
  
  @Override
  public void run() 
  {   
      
      while ( !carr.isTerminadaCarrera() && carr.getVueltas() <= 3  )    //Mientras la carrera no haya terminado
      {
          if (carr.getTerminado()){
              this.recorrido=0;
          }
          this.recorrido+=5+(ran.nextInt(45)+1);    
          carr.AvanzaHilo(Nombre, recorrido);     //Comprobamos el avance del hilo
          
          try {
                Thread.sleep(1000);              //Lo detenemos un 1seg
                this.recorrido+=this.avanza;     //Avanzamos de nuevo
          } catch (InterruptedException ex) 
          {
              Logger.getLogger(Coche.class.getName()).log(Level.SEVERE, null, ex);
          }
          
      }
      System.out.println("Hilo "+this.Nombre+ " terminado ");     
  }  
  
  
}


public class HilosCarreraJava {

    public static void main(String[] args) 
   { 
    int distancia;  //Distancia que van a recorrer los coches

    HashMap total;
    
    boolean terminado=false;  //Indicamos que la carrera no ha terminado
    
    Random r = new Random();

    distancia=100+( r.nextInt(400)+1);   //Entre 100 y 500 metros
    
    Carrera carr = new Carrera(distancia,terminado);  
    
    String cars[];
   
    cars =new String[3];

    cars[0]="Opel";
    cars[1]="Ford";
    cars[2]="Seat";
    
    System.out.println("----LA CARRERA HA COMENZADO ----- DISTANCIA :"+distancia);
    
    Thread[] hilos = new Thread[3];  //Creamos el array de hilos

    
    for (int i=0;i<cars.length;i++)  
    {
    //Elegimos un avance para ese coche entre 5-50
       hilos[i]= new Thread(new Coche(cars[i],carr) );
    
       hilos[i].start();   //Lanzamos el hilo
    
    }
    
    for (Thread h: hilos)   //Para cade hilo hacemos que el principal espere
    {
      try {
        h.join();
      } catch (InterruptedException e) {
      }
    }
    
    System.out.println("----El podium quedaría así -----");
    
    total = carr.getTotal();
    
    List<Map.Entry<String,Integer>> list = new ArrayList<>(carr.getTotal().entrySet());   //Convertimos el map a un arraylist
               
    list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
               
    list.forEach(System.out::println);
    
    System.out.println("----LA CARRERA HA TERMINADO -----");
    
   }
}

