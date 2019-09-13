#ifndef CHARACTER_HPP
#define CHARACTER_HPP

#include <SFML/Graphics.hpp>
#include <iostream>
#include <cmath>

#include <Graphogame/GameSound.hpp>
#include <Graphogame/Input.hpp>


using namespace sf;

class Input;

class Character {

  private:
    Texture       characTex;
    Sprite        character;
    double        speed;

  public:

    // Constructeur vide
    Character();

    // Getters
    Sprite &getCharacter();

    // Mouvement du personnage : gravitation + saut
    void updateCharacter(Input &input, GameSound &, Time);

    // Dessiner le personnage
    void drawCharacter(RenderWindow &);

};

#include "Input.hpp"

#endif
