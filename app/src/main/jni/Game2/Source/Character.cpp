#include <Graphogame/Character.hpp>



Character::Character(){

  // Chargement de l'image du personnage
  Image characImg;
  if (!characImg.loadFromFile("Game2/Images/fish2.png")){
      std::cerr << "Character image is invalid" << std::endl;
      std::exit(EXIT_FAILURE);
  }
  characImg.createMaskFromColor(Color(29, 52, 68));
  if (!characTex.loadFromImage(characImg)){
      std::cerr << "Fail to get the texture of the character image" << std::endl;
      std::exit(EXIT_FAILURE);
  }
  characTex.setSmooth(true);
  character.setTexture(characTex);
  character.setScale(0.2, 0.2);
  character.setPosition(30, 400);

  // Initialisation des attributs restants
  speed = 0.2; // doit être modifiable pour le patient
}



Sprite &Character::getCharacter(){
  return character;
}



void Character::updateCharacter(Input &input, GameSound &gameSound, Time elapsed){

  // Sens de déplacement
  int sens = 0;
  if (input.getButton().up){
    sens = -1; // car y croissant vers le bas
    character.setRotation(345.f);
  }
  else if (input.getButton().down){
    sens = 1; // idem
    character.setRotation(15.f);
  }

  if (sens){
    gameSound.playSwimSound();
    // Mise à jour de la position du personnage
    Vector2f position = character.getPosition();
    position.y += sens*elapsed.asMilliseconds()*speed;
    if (0 < position.y && position.y < 740) // le personnage ne peut pas passer au-dessus de la vue pour contourner les obstacles
      character.setPosition(position);
    return;
  }
  character.setRotation(0.f);
}



void Character::drawCharacter(RenderWindow &window){
  window.draw(character);
}
