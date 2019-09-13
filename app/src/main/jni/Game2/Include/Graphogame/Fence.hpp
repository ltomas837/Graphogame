#ifndef Fence_HPP
#define Fence_HPP

#include <SFML/Graphics.hpp>
#include <math.h>

#include <Graphogame/GameSound.hpp>

using namespace sf;

class Fence {

  public:

    enum Direction{ Up=0, Down };

    // Getters
    Sprite &getFenceUp();
    Sprite &getFenceDown();
    int getGap();
    Direction getDirection();

    // Constructeur vide
    Fence();

    // Initialisation des sprites
    void initFence(Texture &, Texture &, Fence &, Vector2f, bool);

    // Mouvement des obstacles en fonction du temps écoulé et de la vitesse de jeu + mise à jour du score du joueur
    void updateFence(RenderWindow &, Fence &, GameSound &, float *, double, Time);

    // Dessine l'obstacle correspondant dans la fenêtre
    void drawFence(RenderWindow &);


  private:
    Sprite fenceUp;
    Sprite fenceDown;
    int    gap;
     // le sens vers lequel évolue la grotte par rapport à l'obstacle considéré : 0 vers le haut, 1 vers le bas
    Direction direction;
    int offset;

    int getOffset();

};

#endif
