#include <Graphogame/Background.hpp>




/**
 * Chargement de l'image de fond
 */
Background::Background() {

  if (!backgroundTex.loadFromFile("Game1/Images/sky.png")){
    std::cerr << "Background image is invalid" << std::endl;
    std::exit(EXIT_FAILURE);
  }
  backgroundTex.setSmooth(true); // Lisse les contours
  background.setTexture(backgroundTex);
}


/**
 * Redimensionnement de l'image de fond afin qu'elle soit adaptée à la fenêtre de jeu, quel que soit la taille d'écran de l'appareil
 *
 * @param window : Fenêtre de jeu
 */
void Background::resizeBackground(RenderWindow &window){

    sf::Vector2u TextureSize = backgroundTex.getSize();     // Taille de l'image
    sf::Vector2u WindowSize = window.getSize();             // Taille de la fenêtre de jeu

    float ScaleX = (float) WindowSize.x / TextureSize.x;
    float ScaleY = (float) WindowSize.y / TextureSize.y;

    /* Remise à l'échelle */
    background.setScale(ScaleX, ScaleY);
}


