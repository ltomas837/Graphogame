#include <Graphogame/Menu.hpp>





Menu::Menu(){

    /* Chargement de l'image du menu principal */
    menuTex.loadFromFile("Game1/Images/menu.png");
    menu.setTexture(menuTex);

    /* Chargement de l'image du menu affiché lorsque le patient a terminé l'activité */
    exitMenuTex.loadFromFile("Game1/Images/exitMenu.png");
    exitMenu.setTexture(exitMenuTex);

    /* Initialisation des attributs restants */
	onMenu = false;
    _againMenuItems.clear();
}


/**
 * Redimensionnement du menu afin qu'il soit adapté à la fenêtre de jeu, quel que soit la taille d'écran de l'appareil
 *
 * @param window
 */
void Menu::resizeMenu(sf::RenderWindow &window){

    /* Coordonnées des menus en fonction de la taille de la fenêtre de jeu */
    Vector2u size = window.getSize();
    menu.setPosition(size.x/3, size.y/3);
    exitMenu.setPosition(size.x/3, size.y/3);

    Vector2f menuPosition = menu.getPosition();
    Vector2f exitMenuPosition = exitMenu.getPosition();

    /* Coordonnées du bouton 'Rejouer' pour le menu principal */
    MenuItem playButton;
    playButton.rect.top= static_cast<int>(menuPosition.y);
    playButton.rect.height = static_cast<int>(230 * size.y / 1536);
    playButton.rect.left = static_cast<int>(menuPosition.x);
    playButton.rect.width = static_cast<int>(721 * size.x / 2048);
    playButton.action = Play;

    /* Coordonnées du bouton 'Quitter' pour le menu principal */
    MenuItem exitButton;
    exitButton.rect.top = static_cast<int>(230 * size.y / 1536 + menuPosition.y);
    exitButton.rect.height = static_cast<int>(240 * size.y / 1536);
    exitButton.rect.left = static_cast<int>(menuPosition.x);
    exitButton.rect.width = static_cast<int>(721 * size.x / 2048);
    exitButton.action = Exit;

    _againMenuItems.push_back(playButton);
    _againMenuItems.push_back(exitButton);

    /* Coordonnées du bouton 'Quitter' pour le menu affiché lorsque le patient a terminé l'activité */
    MenuItem bigExitButton;
    bigExitButton.rect.top= static_cast<int>(exitMenuPosition.y);
    bigExitButton.rect.height = static_cast<int>(470 * size.y / 1536);
    bigExitButton.rect.left = static_cast<int>(exitMenuPosition.x);
    bigExitButton.rect.width = static_cast<int>(721 * size.x / 2048);
    bigExitButton.action = Exit;

    _exitMenuItem = bigExitButton;
}


/**
 * Retourne l'action de jeu à exécuter selon l'endroit clické lorsque le menu principal est à l'écran
 *
 * @param x Coordonnée selon l'axe des abscisse du click
 * @param y Coordonnée selon l'axe des ordonnées du click
 * @return
 */
Menu::MenuResult Menu::HandleClickAgainMenu(int x, int y){

	std::list<MenuItem>::iterator it;

	/* On regarde si le click est sur un bouton */
	for ( it = _againMenuItems.begin(); it != _againMenuItems.end(); it++){
		sf::Rect<int> menuItemRect = (*it).rect;
	 	if( (menuItemRect.top + menuItemRect.height) > y
	  	&& menuItemRect.top < y
	   	&& menuItemRect.left < x
	   	&& (menuItemRect.left + menuItemRect.width) > x){
	 	    /* Retourne l'action relative au bouton */
	  	    return (*it).action;
		}
	}

	return Nothing;
}


/**
 * Retourne l'action de jeu à exécuter selon l'endroit clické lorsque le menu affiché lorsque le patient a terminé l'activité est à l'écran
 *
 * @param x Coordonnée selon l'axe des abscisse du click
 * @param y Coordonnée selon l'axe des ordonnées du click
 * @return
 */
Menu::MenuResult Menu::HandleClickExitMenu(int x, int y){

    sf::Rect<int> menuItemRect = _exitMenuItem.rect;

    /* On regarde si le click est sur un bouton */
    if( (menuItemRect.top + menuItemRect.height) > y
        && menuItemRect.top < y
        && menuItemRect.left < x
        && (menuItemRect.left + menuItemRect.width) > x){
        return _exitMenuItem.action;
    }

    return Nothing;
}


/**
 * Traitement des évènements quand un menu est affiché
 *
 * @param window : Fenêtre de jeu
 * @param input : Entrée à gérer
 * @param activityIsFinished : Booléen indiquant si l'activité est finie
 * @return : L'action a effectuée suite au click
 */
Menu::MenuResult Menu::GetMenuResponse(sf::RenderWindow &window, Input *input, bool activityIsFinished){
	sf::Event menuEvent;

	/* Boucle des événements */
	while(input->getActive() ? window.pollEvent(menuEvent) : window.waitEvent(menuEvent)) { // faire avec active cf input.cpp

        switch (menuEvent.type) {

            /* Fenêtre fermé de façon inattendue */
            case sf::Event::Closed:
                mutex2.lock();
                input->setReadData(false);
                mutex2.unlock();
                input->getReader().wait();
                window.close();
                return Exit;

            /* Gestion des cas où l'application perd le focus afin que l'application ne consomme pas trop de ressource si elle est situé en tâche de fond */
            case sf::Event::LostFocus:
                input->setActive(false);
                return Nothing;
            case sf::Event::GainedFocus:
                input->setActive(true);
                return Nothing;
            case sf::Event::MouseLeft:
                input->setActive(false);
                return Nothing;
            case sf::Event::MouseEntered:
                input->setActive(true);
                return Nothing;

            /* Si l'écran est touché... */
            case sf::Event::TouchBegan:

                /* Ne fonctionne pas mais normalement détecte le doigt, il doit s'agir de l'index pour que le contact soit compté */
                if (menuEvent.touch.finger == 0){

                    Menu::MenuResult result;

                    /* Action à effectuer en fonction du menu et des coordonnées du click */
                    if (activityIsFinished) result = HandleClickExitMenu(menuEvent.touch.x, menuEvent.touch.y);
                    else result = HandleClickAgainMenu(menuEvent.touch.x, menuEvent.touch.y);

                    /* Fermeture du jeu en terminant d'abord le thread récupérant les données bluetooth depuis l'application à travers la socket TCP */
                    if (result == Menu::Exit){
                        mutex2.lock();
                        input->setReadData(false);
                        mutex2.unlock();
                        input->getReader().wait();
                        window.close();
                    }
                    return result;
                }
                return Nothing;

            default:
                return Nothing;
        }
	}
}


/**
 * Dessiner le menu principal ou le menu affiché lorsque le patient a terminé l'activité
 *
 * @param window : Fenêtre de jeu
 * @param game : Instance du jeu
 * @return
 */
Menu::MenuResult Menu::drawMenu(sf::RenderWindow &window, Game &game){

    /* Dessiner le fond */
    game.render();

    /* Acitivité finie ? */
    mutex3.lock();
    int remainedTime = game.getRemainedTime();
    int activityElapsedTime = game.getActivityElapsedTime();
    mutex3.unlock();
    bool activityIsFinished = (remainedTime <= activityElapsedTime);

    /* Dessiner le menu adéquat à la situation (activité finie ou non) */
    if (activityIsFinished) window.draw(exitMenu);
	else window.draw(menu);

    /* Affichage de la fenêtre de jeu */
	window.display();

	return GetMenuResponse(window, game.getInput(), activityIsFinished);
}

