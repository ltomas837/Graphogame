#ifndef PROJECT_BACKGROUND_HPP
#define PROJECT_BACKGROUND_HPP

#include <SFML/Graphics.hpp>
#include <SFML/Window.hpp>
#include <iostream>

using namespace sf;


/**
 * Classe représentant l'image de fond
 */
class Background {

  private:
      Texture backgroundTex;
      Sprite background;

  public:

      /** Constructeur */
      Background();

      /** Getters */
      Texture &getTexture(){ return backgroundTex; }

      /** Redimensionner l'image de fond */
      void resizeBackground(RenderWindow &window);

      /** Dessiner le background */
      void drawBackground(RenderWindow &window) {window.draw(background);}

};

#endif
