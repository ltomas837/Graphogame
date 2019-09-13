#include <Graphogame/Fence.hpp>



Fence::Fence(){}



void Fence::initFence(Texture &fenceUpTex, Texture &fenceDownTex, Fence &beforeCurrent, Vector2f position, bool random){


  fenceUp.setTexture(fenceUpTex);
  fenceDown.setTexture(fenceDownTex);
  int cmNb; // = 3;// = rand()%3 + 3; // c'est ça qu'on va modifier pour faire varier le gap minimum qu'il peut y avoir dans la grotte
  if (random){
    //cmNb = rand()%4 + 3; // 3 < cmNb < ?
    cmNb = 4;
    gap = 45*cmNb;
    int previousOffset = beforeCurrent.getOffset();//30;//60;

    Vector2f beforePosition = beforeCurrent.getFenceUp().getPosition();

    bool keepDirection;
    if (rand()%5 < 4) keepDirection = (-beforePosition.y >= 125 + gap + previousOffset) && (-beforePosition.y <= 840 - previousOffset);
    else keepDirection = false;

    if (keepDirection){
      direction = beforeCurrent.getDirection();
      offset = previousOffset;
    }
    else{
      direction = Direction ((beforeCurrent.getDirection()+1)%2);
      offset = (int) gap/6 + rand()%((int) gap/6);
    }

    int randomY;
    if (direction) randomY = -beforePosition.y - offset;
    else randomY = -beforePosition.y + offset;


    //int randomY = rand()%(715-gap) + gap + 125;
    //int randomY = 715-gap + gap +125; // tout en haut
    //int randomY = gap +125; // tout en bas
    position.y = -randomY;
    fenceUp.setPosition(position);
    position.y += 867 + gap;
    fenceDown.setPosition(position);


  }

  else{
    //cmNb = 7;
    cmNb = 4;
    gap = 45*cmNb;
    position.y = -450 - gap/2;
    fenceUp.setPosition(position);
    position.y += 867 + gap;
    fenceDown.setPosition(position);
    //direction = Direction (rand()%2);
    direction = Up;
    offset = (int) gap/6;
  }

  fenceUp.scale((float) 274/(512*2), (float) 274/512);
  fenceDown.scale((float) 274/(512*2), (float) 274/512);

}



void Fence::updateFence(RenderWindow &window, Fence &beforeCurrent, GameSound &gameSound, float *score, double speed, Time elapsed){

  Vector2f position = fenceUp.getPosition();
  position.x -= elapsed.asMilliseconds()*speed; // faire en fonction de la vitesse du jeu

  if (position.x < (int) -window.getSize().x/10){
    Vector2f beforePosition = beforeCurrent.getFenceUp().getPosition();
    //position.x += window.getSize().x + 2*window.getSize().x/10;// - 5; // on calcule sa nouvelle position
    position.x += beforePosition.x + 2*window.getSize().x/10 -2; // on calcule sa nouvelle position

    int cmNb = 4;
    gap = 45*cmNb;
    int previousOffset = beforeCurrent.getOffset();//30;//60;


    bool keepDirection;
    if (rand()%5 < 4) keepDirection = (-beforePosition.y >= 125 + gap + previousOffset) && (-beforePosition.y <= 840 - previousOffset);
    else keepDirection = false;

    if (keepDirection){
      direction = beforeCurrent.getDirection();
      offset = previousOffset;
    }
    else{
      direction = Direction ((beforeCurrent.getDirection()+1)%2);
      offset = (int) gap/6 + rand()%((int) gap/6);
    }

    int randomY;
    if (direction) randomY = -beforePosition.y - offset;
    else randomY = -beforePosition.y + offset;
    position.y = -randomY;

    //int randomY = rand()%(715-gap) + gap + 125;
    //int randomY = 715-gap + gap +125; // tout en haut
    //int randomY = gap +125; // tout en bas
    // position.y = -randomY;
    // fenceUp.setPosition(position);
    // position.y += 867 + gap;
    // fenceDown.setPosition(position);

  }

  *score += (float) elapsed.asMilliseconds()/10000;
  if ((*score/10 - floorf(*score/10) < 0.001) && !gameSound.getScoreSoundCurrentlyPlayed()){
    gameSound.setScoreSoundCurrentlyPlayed(true);
    gameSound.playScoreSound();
  }
  else if ((*score+5.)/10 - floorf((*score+5.)/10) < 0.001){
    gameSound.setScoreSoundCurrentlyPlayed(false);
  }
  //if (score/10000 - )
  //gameSound.playScoreSound();
  fenceUp.setPosition(position);
  position.y += 867 + gap;
  fenceDown.setPosition(position);

}



void Fence::drawFence(RenderWindow &window){
  window.draw(fenceUp);
  window.draw(fenceDown);
}



Sprite &Fence::getFenceUp(){
  return fenceUp;
}



Sprite &Fence::getFenceDown(){
  return fenceDown;
}



Fence::Direction Fence::getDirection(){
  return direction;
}



int Fence::getGap(){
  return gap;
}



int Fence::getOffset(){
  return offset;
}
