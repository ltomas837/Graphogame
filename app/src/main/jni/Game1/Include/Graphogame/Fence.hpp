#ifndef FENCE_HPP
#define FENCE_HPP

#include <cmath>

#include <SFML/Graphics.hpp>
#include <Graphogame/GameSound.hpp>



using namespace sf;

/**
 * Classe représentant les obstacles en bois
 */

class Fence {

  private:
    Sprite fenceUp;
    Sprite fenceDown;

    /** Indique si l'obstacle est dépassé par le personnage afin de mettre à jour le score */
    bool   passed;

  public:

    /** Constructeur vide */
    Fence(){}

    /** Getters */
    Sprite &getFenceUp(){return fenceUp;}
    Sprite &getFenceDown(){return fenceDown;}

    /** Initialisation des sprites */
    void initFence(RenderWindow &window, Texture &, Texture &, Fence &, Vector2f, bool);

    /** Mise à jour de la position des obstacles */
    void updateFence(RenderWindow &window, Fence &, GameSound &, int *, double, Time);

    /** Dessiner la pair d'obstacle */
    void drawFence(RenderWindow &window){
        window.draw(fenceUp);
        window.draw(fenceDown);
    }


};

#endif
