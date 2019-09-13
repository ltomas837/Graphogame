#ifndef PROJECT_BACKGROUND_HPP
#define PROJECT_BACKGROUND_HPP

#include <SFML/Graphics.hpp>
#include <iostream>

using namespace sf;

class Background {

  private:
      Texture backgroundTex;
      Sprite background;

  public:

      // Constructeur
      Background();

      // Dessiner le background
      void drawBackground(RenderWindow &window);

};

#endif
