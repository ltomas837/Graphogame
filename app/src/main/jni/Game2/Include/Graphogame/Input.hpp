#ifndef INPUT_HPP
#define INPUT_HPP

#include <SFML/Graphics.hpp>

#include "Character.hpp"

using namespace sf;

class Input {

  private:
      struct Button {
          bool up, down, right, left, jump, enter;
      };
      Button button;
      Event event;

  public:

      // Constructeur, on initialise toutes les entrées à faux
      Input();

      // Accesseur des entrées
      Button getButton() const;

      // Gestion des entrées dans la boule de jeu
      void inputManagement(RenderWindow &);

};

#endif
