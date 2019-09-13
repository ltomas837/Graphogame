#ifndef RULES_HPP
#define RULES_HPP


#include <SFML/Graphics.hpp>


class Game;
class Input;

using namespace sf;


class Rules {

private:
    struct RulesItem{
    public:
        sf::Rect<int> rect;
    };

    /** Règles de jeu 'fréquence' */
    Texture rulesFrTex;
    Sprite rulesFr;

    /** Règles du jeu 'force' */
    Texture rulesFoTex;
    Sprite rulesFo;

    /** Booléen indiquant si les règles de jeu sont affichées */
    bool onRules;

    /** Bouton 'OK' */
    RulesItem btnOK;

    /** Traitement des évènements quand les règles de jeu sont affichées */
    void GetRulesResponse(RenderWindow &, Game &);

public:

    /** Constructeur */
    Rules();

    /** Getters */
    bool *getOnRules() {return &onRules;}

    /** Dessiner les règles de jeu */
    void drawRules(RenderWindow &, Game &);

    /** Redimensionnements des règles de jeu */
    void resizeRules(sf::RenderWindow &);

    /** Indique quelle action doit ere effectuée lorsqu'un click est perçu et que les règles de jeu sont affichées */
    bool HandleClick(int x, int y);
};

#include <Graphogame/Game.hpp>

#endif //RULES_HPP
