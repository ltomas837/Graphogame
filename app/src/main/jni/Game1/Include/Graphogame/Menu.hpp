#ifndef MENU_HPP
#define MENU_HPP

#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include <SFML/Graphics/Rect.hpp>


#include <thread>
#include <list>


class Game;

class Input;


/**
 * Classe représentant les menus de jeu
 *
 */
class Menu{

public:

	enum MenuResult { Nothing, Exit, Play };

	/** Constructeur */
	Menu();

	/** Redimensionnement des menus */
    void resizeMenu(sf::RenderWindow &);

    /** Getters */
	bool *getOnMenu() {return &onMenu;}

	/** Setters */
	void setOnMenu(bool onMenu) {this->onMenu = onMenu;}

	/** Dessiner le menu */
	MenuResult drawMenu(sf::RenderWindow& window, Game &);

private:
	struct MenuItem{
		sf::Rect<int> rect;
		MenuResult action;
	};

	/** Traitement des évènements quand un menu est affiché */
	MenuResult GetMenuResponse(sf::RenderWindow &, Input *, bool);

	/** Indique quelle action doit ere effectuée lorsqu'un click est perçu et que le menu principal est à l'écran */
 	MenuResult HandleClickAgainMenu(int x, int y);

	/** Indique quelle action doit ere effectuée lorsqu'un click est perçu et que le menu affiché lorsque le patient a terminé l'activité est à l'écran */
	MenuResult HandleClickExitMenu(int x, int y);

	/** Liste de boutons du menu principal */
 	std::list<MenuItem> _againMenuItems;

	/** Unique bouton du menu affiché lorsque le patient a terminé l'activité */
 	MenuItem _exitMenuItem;

	/** Booléen indiquant si un menu est affiché */
	bool onMenu;

	/** Menu principal */
	sf::Texture menuTex;
	sf::Sprite menu;

	/** Menu affiché lorsque le patient a terminé l'activité */
    sf::Texture exitMenuTex;
    sf::Sprite exitMenu;

};

#include <Graphogame/Game.hpp>

#endif
