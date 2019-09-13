#ifndef CHARACTER_HPP
#define CHARACTER_HPP

#include <SFML/Graphics.hpp>
#include <iostream>
#include <cmath>

#include <Graphogame/GameSound.hpp>



using namespace sf;

class Input;


/**
 * Classe représentant le personnage de jeu
 */

class Character {

  private:
    Texture       characTex;
    Sprite        character;

    /** Horloge indiquant la date du dernier saut effectué */
    Clock         lastJump;

    /** Ordonnée correspondant à l'ordonnée où était le personnage lors du dernier saut, sert à calculer la trajectoire parabolique du personnage */
    float         lastJumpY;

    /** Booléen indiquant si le capteur de force a été relâché depuis le dernier saut */
    bool          isJumping;

  public:

    /** Constructeur vide */
    Character();

    /** Getters */
    Sprite &getCharacter(){return character;}

    /** Redimensionnement du personnage */
    void resizeCharacter(RenderWindow &window, Texture &backgroundTex);

    /** Mouvement du personnage */
    void updateCharacter(RenderWindow &, GameSound &, bool);

    /** Dessiner le personnage */
    void drawCharacter(RenderWindow &window){window.draw(character);}

};

#include "Input.hpp"

#endif
