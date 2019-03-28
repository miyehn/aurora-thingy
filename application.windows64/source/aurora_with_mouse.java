import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class aurora_with_mouse extends PApplet {

/*
Author: Rain Du
Email: rainduym@gmail.com
Created in March 2016
This work is licensed under the Creative Commons 
Attribution-NonCommercial-ShareAlike 4.0 International License. 
To view a copy of this license, visit 
http://creativecommons.org/licenses/by-nc-sa/4.0/.
*/

ArrayList<Aurora> all = new ArrayList<Aurora>();

public void setup()
{
  
  
  noStroke();
  frameRate(30);
}

public void draw()
{
  background(0,0,0);
  println(all.size());
  
  for(int i=all.size()-1; i>=0; i--)
  {
    if(all.get(i).dead)
      all.remove(i);
    else
      all.get(i).aurora();
  }
}

public void mousePressed()
{
  int R = (int)random(180,220);
  int G = (int)random(200,255);
  int B = (int)random(200,255);
  int col = 0xff<<24 | R<<16 | G<<8 | B;
  println(hex(col));
  Aurora A = new Aurora(180,10,5,30,col,6,0);
  all.add(A);
}

public void mouseReleased()
{
  for(int i=0; i<all.size(); i++)
    all.get(i).locked = true;
}
class Aurora
{
  int numDots;
  LaggedDot[] dots;
  int auroraColor;
  boolean locked = false;
  boolean dead = false;
  
  //default constructor
  Aurora(int numDots, int auroraColor)
  {
    this.auroraColor = auroraColor;
    this.numDots = numDots;
    this.dots = new LaggedDot[numDots];
    for(int i=0; i<numDots; i++)
      dots[i] = new LaggedDot(10,5,30,auroraColor,6,0);
  }
  
  //full constructor
  Aurora(int numDots, float radius, float minRadius, int tailLength, 
                      int auroraColor, float maxAlpha, float minAlpha)
  {
    this.auroraColor = auroraColor;
    this.numDots = numDots;
    this.dots = new LaggedDot[numDots];
    for(int i=0; i<numDots; i++)
      dots[i] = new LaggedDot(radius,minRadius,tailLength,auroraColor,maxAlpha,minAlpha);
  }
  
  public void aurora()//close to linear
  {
    if(!locked && mousePressed)
    {
      for(int i=dots.length-1; i>=0; i--)
      {
        if(i==dots.length-1) dots[i].follow(mouseX, mouseY, (i+1+20)/20.0f);
        else
          dots[i].follow(dots[i+1].positionX, dots[i+1].positionY, (i+1+20)/20.0f);
      }
    }
    else if (outOfSight())
    {
      dead = true;
    }
    else if(!cleared())
    {
      for(int i=0; i<dots.length; i++)
        dots[i].fallGravity(dots[i].prevX, dots[i].prevY, dots[i].positionX, dots[i].positionY, 0.7f);
    }
  }
  
  public void aurora2()//all follow the mouse - more 2-dimensional
  {
    if(!locked && mousePressed)
    {
      for(int i=dots.length-1; i>=0; i--)
      {
        dots[i].follow(mouseX, mouseY, (i+1+30)/50.0f);
      }
    }
    else if (outOfSight())
    {
      dead = true;
    }
    else if(!cleared())
    {
      for(int i=0; i<dots.length; i++)
        dots[i].fallGravity(dots[i].prevX, dots[i].prevY, dots[i].positionX, dots[i].positionY, 0.7f);
    }
  }
  
  //----helpers----
  public boolean cleared()
  {
    for(int i=0; i<dots.length; i++)
    {
      for(int j=0; j<dots[i].tailLength; j++)
        if(dots[i].used[j] == true) return false;
    }
    return true;
  }

  public boolean outOfSight()
  {
    for(int i=0; i<dots.length; i++)
      if(dots[i].tailEndY-dots[i].minRadius/2 <= height) return false;
    return true;
  }
}
class LaggedDot
{
  float radius;
  float minRadius;
  int tailLength;//number of dots to be drawn TOTAL. >0
  float prevX;//??
  float prevY;//??
  float positionX;
  float positionY;
  float tailEndX;//approximate
  float tailEndY;//approximate
  float[] xtail;
  float[] ytail;
  boolean[] used;
  int index;
  int fillColor;
  float maxAlpha;
  float minAlpha;
  
  LaggedDot(float radius, float minRadius, int tailLength, 
            int fillColor, float maxAlpha, float minAlpha)
  {
    this.radius = radius;
    this.minRadius = minRadius;
    this.tailLength = tailLength;
    this.xtail = new float[this.tailLength];
    this.ytail = new float[this.tailLength];
    this.used = new boolean[this.tailLength];
    for(int i=0; i<this.tailLength; i++) used[i] = false;
    this.index = 0;
    this.fillColor = fillColor;
    this.maxAlpha = maxAlpha;
    this.minAlpha = minAlpha;
  }
  
  public void move(float x, float y)
  {
    prevX = positionX; prevY = positionY;
    used[index] = true;
    xtail[index] = x;
    ytail[index] = y;
    int count = 0;//count num of dots TOTAL,(1,tailLength)
    for(int i=0; i<tailLength; i++)
      if(used[i] == true) count++;
    for(int i=count-1; i>=0; i--)//draw tail, from smallest to largest
    //range of i (0,count)
      {
        //set color (alpha) based on main color
        //the actual alpha is an int
        int alpha = (int)(minAlpha+(maxAlpha-minAlpha)/tailLength*(count-i));
        fill(fillColor,alpha);
        //set radius and coordinates of each tail
        float r = (radius-minRadius)/count*(count-i) + minRadius;
        //float r = radius;
        float x_current_circ = xtail[prev(index,i)];
        float y_current_circ = ytail[prev(index,i)];
        //draw
        ellipse(x_current_circ,y_current_circ,r,r);
        //set positions of the head
        if(i==0)//set head coordinates
        {
          positionX = x_current_circ; positionY = y_current_circ;
        }
        else if(i==count-1)//set tailEnd coordinates
        {
          tailEndX = x_current_circ; tailEndY = y_current_circ;
        }
      }
      index = next(index,1);
    }
  
  //1 slow; 5 fast
  public void follow(float x, float y, float speed)//constant speed
  {
    if(isStart()
      ||(y==positionY && x==positionX))
      move(x,y);
    else
    {
      float slope = 1.0f*(y-positionY) / (x-positionX);
      float angle = atan(slope);
      //correct angles:
      if(x<positionX) angle += PI;
      float x1 = positionX + cos(angle) * speed;
      float y1 = positionY + sin(angle) * speed;
      move(x1, y1);
    }
  }

  //0.2 slow; 0.8 fast
  public void follow2(float x, float y, float speed)//variable speed (slow down)
  {
    if(isStart())
    {  
      move(x,y);
    }
    else
    {
      float x1 = positionX + (x-positionX) * speed;
      float y1 = positionY + (y-positionY) * speed;
      move(x1, y1);
    }
  }
  //                      prevX       prevY        posX        posY
  public void fallGravity(float initX, float initY, float finX, float finY, float grav)
  {
    finX += (finX-initX)*0.97f;//times friction: 0.9~1.0
    finY += finY-initY + grav/10;
    move(finX,finY);
  }
  
  public void fallVertical(float initX, float initY, float finX, float finY, float grav)
  {
    finY += finY-initY + grav/10;
    move(finX, finY);
  }


//----HELPERS----
  private int prev(int ind, int steps)
  {
    if(ind-steps<0) return ind-steps+tailLength;
    else return ind-steps;
  }
  
  private int next(int ind, int steps)
  {
    if(ind+steps>=tailLength) return ind+steps-tailLength;
    else return ind+steps;
  }
  
  public void clear()
  {
    for(int i=0; i<tailLength; i++)
      used[i] = false;
  }
  
  public boolean cleared()
  {
    for(int i=0; i<tailLength; i++)
      if(used[i] == true) return false;
    return true;
  }
  
  private boolean isStart()
  {
    int count = 0;
    for (int i=0; i<tailLength; i++)
    {
      if(used[i] == true) count++;
    }
    if (count>0)return false;
    return true;
  }
}
  public void settings() {  size(400,350);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "aurora_with_mouse" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
