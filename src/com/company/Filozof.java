package com.company;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Filozof extends Thread
{
    static int MAX;
    static Semaphore[] widelec;
    int mojNum;
    int typ;
    Random losuj;

    public Filozof(int nr, int typ)
    {
        mojNum = nr;
        this.typ = typ;
        if(typ == 3)
            losuj = new Random(mojNum);
    }

    public void run()
    {
        while(true)
        {
            System.out.println("Mysle ¦ " + mojNum);
            try
            {
                Thread.sleep((long)(7000 * Math.random()));
            }
            catch (InterruptedException e){}

            if(typ == 1)
            {
                widelec[mojNum].acquireUninterruptibly();
                widelec[(mojNum + 1)%MAX].acquireUninterruptibly();
            }
            else if(typ == 2)
            {
                if(mojNum == 0)
                {
                    widelec[(mojNum + 1)%MAX].acquireUninterruptibly();
                    widelec[mojNum].acquireUninterruptibly();
                }
                else
                {
                    widelec[mojNum].acquireUninterruptibly();
                    widelec[(mojNum + 1)%MAX].acquireUninterruptibly();
                }
            }
            else if(typ == 3)
            {
                int strona = losuj.nextInt(2);
                boolean podnioslDwaWidelce = false;
                do {
                    if(strona == 0)
                    {
                        widelec[mojNum].acquireUninterruptibly();
                        if(!(widelec[(mojNum + 1)%MAX].tryAcquire()))
                            widelec[mojNum].release();
                        else
                            podnioslDwaWidelce = true;
                    }
                    else
                    {
                        widelec[(mojNum + 1)%MAX].acquireUninterruptibly();
                        if(!(widelec[mojNum].tryAcquire()))
                            widelec[(mojNum + 1)%MAX].release();
                        else
                            podnioslDwaWidelce = true;
                    }
                } while (!podnioslDwaWidelce);
            }
            System.out.println(("Zaczyna jesc " + mojNum));
            try
            {
                Thread.sleep((long)(5000 * Math.random()));
            }
            catch (InterruptedException e){}
            System.out.println ("Konczy jesc " + mojNum) ;
            widelec[mojNum].release();
            widelec[(mojNum+1)%MAX].release();
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("1. Problem filozofów");
        System.out.println("2. Problem ucztujących filozofów z niesymetrycznym sięganiem po widelce");
        System.out.println("3. Rzut monety w rozwiązaniu problemu ucztujących Filozofów");
        System.out.println("Wybierz który wariant wybierasz: ");
        int typ = scan.nextInt();

        while(!(typ == 1 || typ == 2 || typ == 3))
        {
            System.out.println("Niepoprawny wariant!");
            System.out.println("Wybierz który wariant wybierasz: ");
            typ = scan.nextInt();
        }

        System.out.println("Wybierz ilość Filozofów (od 2 do 100): ");
        MAX = scan.nextInt();

        while(MAX > 100 || MAX < 2)
        {
            System.out.println("Niepoprawny wariant!");
            System.out.println("Wybierz ilość Filozofów (od 2 do 100): ");
            MAX = scan.nextInt();
        }
        widelec = new Semaphore [MAX];

        for(int i = 0; i < MAX; i++)
            widelec[i] = new Semaphore(1);
        for(int i = 0; i < MAX; i++)
            new Filozof(i, typ).start();
    }
}