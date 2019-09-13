 #include <Graphogame/Fence.hpp>




/**
 * Initialiser les obstacles afin de féinir leur position initiale
 *
 * @param window : Fenêtre de jeu
 * @param fenceUpTex : Texture, pour ne pas à avoir à chaque obstacle, gain de performance selon la documentation, sûrement par pour 5 ?... Pas eu le temps de changer
 * @param fenceDownTex : Texture, pour ne pas à avoir à chaque obstacle, gain de performance selon la documentation
 * @param beforeCurrent : Obstacles situé en dernier
 * @param position : Position actuel de la pair d'obstacle (l'abscisse étant fixé)
 * @param random : ouverture aléatoire ou non de la pair d'obstacles
 */
void Fence::initFence(RenderWindow &window, Texture &fenceUpTex, Texture &fenceDownTex, Fence &beforeCurrent, Vector2f position, bool random){

  /* Définition de la taille de l'ouverture entre les deux obstacles */
  int cmNb = 0; // c'est ça qu'on modofie pour faire varier la taille d'ouverture, de 0 à 3
  float gap = static_cast<float>(45 * (5+cmNb) * window.getSize().y / 768);

  float randomY;

  /* Si l'ouverture est aléatoire */
  if (random){
      /* On calcule la nouvelle ouverture entre la paire d'obstacle aléatoirement */
    float beforeY = fabsf(beforeCurrent.getFenceUp().getPosition().y);
    randomY = rand()%(511-45*cmNb)*window.getSize().y/768 +  (80 + 45*cmNb)*window.getSize().y/768;

      /* Néanmoins, on ne place pas l'ouverture à l'opposé de l'ouverture de la paire précédente d'obstacles, de manière à ne pas se trouver dans une situation impossible */
    if ( (beforeY < 270*window.getSize().y/768) && (beforeY + 270*window.getSize().y/768 < randomY) )
      randomY = static_cast<float>(beforeY + rand()%(271)*window.getSize().y/768 + (80 + 45*cmNb)*window.getSize().y/768);
  }
  else{ // Seul la première paire d'obstacles passe ici de manière à ne pas se trouver dans une situation impossible
    randomY = static_cast<float>((340 + 45* cmNb) * window.getSize().y / 768);
  }

  /* Initialisation des obstacles */
  fenceUp.setTexture(fenceUpTex);
  position.y = -randomY;
  fenceUp.setPosition(position);

  fenceDown.setTexture(fenceDownTex);
  position.y = static_cast<float> (560*window.getSize().y/768 + gap - randomY); // 560*window.getSize().y/768 : Taille de l'obstacle d'en haut
  fenceDown.setPosition(position);

  /* Modification de l'échelle de leur taille */
  fenceUp.setScale(2.25, 2.25);
  fenceDown.setScale(2.25, 2.25);

  /* Initialisation des attributs restants */
  passed = false;
}


/**
 * Mouvement des obstacles en fonction du temps écoulé et de la vitesse de jeu + mise à jour du score du joueur.
 *
 * @param window : Fenêtre de jeu
 * @param beforeCurrent : Obstacles situé en dernier
 * @param gameSound : Sons de jeu
 * @param score : Score actuel du joueur
 * @param speed : Vitesse de défilement des obstacles
 * @param elapsed : Temps écoulé depuis la dernière boucle de jeu
 */
void Fence::updateFence(RenderWindow &window, Fence &beforeCurrent, GameSound &gameSound, int *score, double speed, Time elapsed){

   /* Définition de la taille de l'ouverture entre les deux obstacles */
  int cmNb = 0; // c'est ça qu'on modofie pour faire varier la taille d'ouverture, de 0 à 3
  float gap = static_cast<float>(45 * (5+cmNb) * window.getSize().y / 768);

  /* Mise à jour de la position horizontal des deux obstacles */
  Vector2f position = fenceUp.getPosition();
  position.x -= elapsed.asMilliseconds()*speed*window.getSize().x/1366; // faire en fonction de la vitesse du jeu


  /* Si l'obstacle n'est plus visible à l'écran..*/
  if (position.x < (float) -100*window.getSize().x/1366){
    /* En pratique cette condition est toujours vérifiée, mais vérification au cas où */
    if (passed) {
        passed = false;

        /* On le place comme le prochain obstacle à apparaître à l'écran */
        position.x += (350*window.getSize().x/1366)*5;

        /* On calcule la nouvelle ouverture entre la paire d'obstacle aléatoirement */
        float beforeY = static_cast<int>(fabsf(beforeCurrent.getFenceUp().getPosition().y));
        float randomY = rand() % (511 - 45 * cmNb) * window.getSize().y / 768 +
                        (80 + 45 * cmNb) * window.getSize().y / 768;

        /* Néanmoins, on ne place pas l'ouverture à l'opposé de l'ouverture de la paire précédente d'obstacles, de manière à ne pas se trouver dans une situation impossible */
        if ((beforeY < 270 * window.getSize().y / 768) &&
            (beforeY + 270 * window.getSize().y / 768 < randomY))
            randomY = static_cast<float>(beforeY + rand() % (271) * window.getSize().y / 768 +
                                         (80 + 45 * cmNb) * window.getSize().y / 768);
        position.y = -randomY;
    }
  }

  /* Si le personnage vient de passer l'obstacle.. */
  else if (position.x < 0 && !passed){
    *score += 1;                 // Actualisation du score
    passed = true;
    gameSound.playScoreSound();  // Son de score
  }

  /* Application des changements de position */
  fenceUp.setPosition(position);
  fenceDown.setPosition(position.x,
                        static_cast<float>(560*window.getSize().y/768 + gap + position.y)); // 560*window.getSize().y/768 : Taille de l'obstacle d'en haut
}


