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
  color fillColor;
  float maxAlpha;
  float minAlpha;
  
  LaggedDot(float radius, float minRadius, int tailLength, 
            color fillColor, float maxAlpha, float minAlpha)
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
  
  void move(float x, float y)
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
  void follow(float x, float y, float speed)//constant speed
  {
    if(isStart()
      ||(y==positionY && x==positionX))
      move(x,y);
    else
    {
      float slope = 1.0*(y-positionY) / (x-positionX);
      float angle = atan(slope);
      //correct angles:
      if(x<positionX) angle += PI;
      float x1 = positionX + cos(angle) * speed;
      float y1 = positionY + sin(angle) * speed;
      move(x1, y1);
    }
  }

  //0.2 slow; 0.8 fast
  void follow2(float x, float y, float speed)//variable speed (slow down)
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
  void fallGravity(float initX, float initY, float finX, float finY, float grav)
  {
    finX += (finX-initX)*0.97;//times friction: 0.9~1.0
    finY += finY-initY + grav/10;
    move(finX,finY);
  }
  
  void fallVertical(float initX, float initY, float finX, float finY, float grav)
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
  
  void clear()
  {
    for(int i=0; i<tailLength; i++)
      used[i] = false;
  }
  
  boolean cleared()
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