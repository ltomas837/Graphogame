#include <Graphogame/Character.hpp>



Character::Character(){

  /* Chargement de l'image du personnage */
  Image characImg;
  if (!characImg.loadFromFile("Game1/Images/bird.png")){
      std::cerr << "Character image is invalid" << std::endl;
      std::exit(EXIT_FAILURE);
  }

  /* Rend invisible le contour du personnage dont la couleur RGB est [136, 111, 91]*/
  characImg.createMaskFromColor(Color(136, 111, 91));
  if (!characTex.loadFromImage(characImg)){
      std::cerr << "Fail to get the texture of the character image" << std::endl;
      std::exit(EXIT_FAILURE);
  }

  characTex.setSmooth(true); // Lisse les contours

  character.setTexture(characTex);
}



/**
 * Redimensionnement du personnage afin qu'il soit adapté à la fenêtre de jeu, quel que soit la taille d'écran de l'appareil
 *
 * @param window
 * @param backgroundTex
 */
void Character::resizeCharacter(RenderWindow &window, Texture &backgroundTex){

  sf::Vector2u TextureSize = backgroundTex.getSize();     // Taille de l'image
  sf::Vector2u WindowSize = window.getSize();             // Taille de la fenêtre de jeu

  float ScaleX = static_cast<float>(WindowSize.x / (TextureSize.x * 1.75));
  float ScaleY = static_cast<float>(WindowSize.y / (TextureSize.y * 1.75));

  /* Remise à l'échelle */
  character.setScale(ScaleX, ScaleY);

  /* On positionne le personnage initialement au milieu de l'écran (par rapport à l'ordonné) */
  lastJumpY = WindowSize.y / 2 - characTex.getSize().y * ScaleY;
  character.setPosition(WindowSize.x / 26, lastJumpY);

  // Initialisation des attributs restants
  isJumping = false;
  character.setRotation(0);
}



/**
 * Met à jour la position du personnage (gravitation + saut).
 *
 * @param window Fenêtre de jeu
 * @param gameSound Les sons de jeu
 * @param jump Booléen indiquant si l'utilisateur est en train d'appuyer sur le capteur de force (supérieurement à un certain seuil pour l'exercice de force)
 */
void Character::updateCharacter(RenderWindow &window, GameSound &gameSound, bool jump){

  /* Si l'utilisateur est en train d'appuyer sur le capteur de force (supérieurement à un certain seuil pour l'exercice de force) */
  if (jump){
    /* Si l'utilisateur n'était pas déjà en train d'appuyer sur le capteur de force (supérieurement à un certain seuil pour l'exercice de force) */
    if (!isJumping){
      /* On fait sauter le personnage */
      isJumping = true;
      lastJumpY = character.getPosition().y;
      lastJump.restart();
      gameSound.playJumpSound();
    }
  }
  else{
    isJumping = false;
  }

  /* Temps écoulé depuis le dernier saut */
  Int32 msDelay = lastJump.getElapsedTime().asMilliseconds();

  /* Position actuel du personnage */
  Vector2f position = character.getPosition();

  /* Mise à jour de la position du personnage, par rapport à où est ce qu'il en est dans sa trajectoire parabolique commencée depuis le dernier saut */
  position.y = static_cast<float>(lastJumpY + ((sqrt(60) * 0.001 * msDelay * 2 - sqrt(60)) * (sqrt(60) * 0.001 * msDelay * 2 - sqrt(60)) // 2 : Diminuer ce nombre diminue la gravitation, pour diminuer la vitesse de jeu par rapport à la difficulté ET à la différence de jeu fréquence/force
          - 60 // Augmenter ce nombre augmente la hauteur du saut du personnage, au cas où cela peut être utile
          ) * window.getSize().y / 768);

  /* Mise à jour de la rotation du personnage */
  float teta = (position.y - lastJumpY)/5;
  if (45 < teta)
    teta = 45;
  if ( teta < 0 )
    character.setRotation(360+teta);//(360+2*teta) : *2 à voir si le saut rotationne de trop
  else
    character.setRotation(teta);

  /* Le personnage ne peut pas passer au-dessus de la vue pour contourner les obstacles */
  if (0 < position.y)
    character.setPosition(position);
}
