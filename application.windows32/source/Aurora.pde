class Aurora
{
  int numDots;
  LaggedDot[] dots;
  color auroraColor;
  boolean locked = false;
  boolean dead = false;
  
  //default constructor
  Aurora(int numDots, color auroraColor)
  {
    this.auroraColor = auroraColor;
    this.numDots = numDots;
    this.dots = new LaggedDot[numDots];
    for(int i=0; i<numDots; i++)
      dots[i] = new LaggedDot(10,5,30,auroraColor,6,0);
  }
  
  //full constructor
  Aurora(int numDots, float radius, float minRadius, int tailLength, 
                      color auroraColor, float maxAlpha, float minAlpha)
  {
    this.auroraColor = auroraColor;
    this.numDots = numDots;
    this.dots = new LaggedDot[numDots];
    for(int i=0; i<numDots; i++)
      dots[i] = new LaggedDot(radius,minRadius,tailLength,auroraColor,maxAlpha,minAlpha);
  }
  
  void aurora()//close to linear
  {
    if(!locked && mousePressed)
    {
      for(int i=dots.length-1; i>=0; i--)
      {
        if(i==dots.length-1) dots[i].follow(mouseX, mouseY, (i+1+20)/20.0);
        else
          dots[i].follow(dots[i+1].positionX, dots[i+1].positionY, (i+1+20)/20.0);
      }
    }
    else if (outOfSight())
    {
      dead = true;
    }
    else if(!cleared())
    {
      for(int i=0; i<dots.length; i++)
        dots[i].fallGravity(dots[i].prevX, dots[i].prevY, dots[i].positionX, dots[i].positionY, 0.7);
    }
  }
  
  void aurora2()//all follow the mouse - more 2-dimensional
  {
    if(!locked && mousePressed)
    {
      for(int i=dots.length-1; i>=0; i--)
      {
        dots[i].follow(mouseX, mouseY, (i+1+30)/50.0);
      }
    }
    else if (outOfSight())
    {
      dead = true;
    }
    else if(!cleared())
    {
      for(int i=0; i<dots.length; i++)
        dots[i].fallGravity(dots[i].prevX, dots[i].prevY, dots[i].positionX, dots[i].positionY, 0.7);
    }
  }
  
  //----helpers----
  boolean cleared()
  {
    for(int i=0; i<dots.length; i++)
    {
      for(int j=0; j<dots[i].tailLength; j++)
        if(dots[i].used[j] == true) return false;
    }
    return true;
  }

  boolean outOfSight()
  {
    for(int i=0; i<dots.length; i++)
      if(dots[i].tailEndY-dots[i].minRadius/2 <= height) return false;
    return true;
  }
}