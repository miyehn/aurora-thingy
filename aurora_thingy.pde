ArrayList<Aurora> all = new ArrayList<Aurora>();

void setup()
{
  size(400,350);
  smooth();
  noStroke();
  frameRate(30);
}

void draw()
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

void mousePressed()
{
  int R = (int)random(180,220);
  int G = (int)random(200,255);
  int B = (int)random(200,255);
  color col = 0xff<<24 | R<<16 | G<<8 | B;
  println(hex(col));
  Aurora A = new Aurora(180,10,5,30,col,6,0);
  all.add(A);
}

void mouseReleased()
{
  for(int i=0; i<all.size(); i++)
    all.get(i).locked = true;
}
