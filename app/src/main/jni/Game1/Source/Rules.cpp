#include <Graphogame/Rules.hpp>



Rules::Rules(){

    /* Chargement de images des règles du jeu de fréquence */
    Image rulesImageFr;
    rulesImageFr.loadFromFile("Game1/Images/rulesFr.png");
    rulesFrTex.loadFromImage(rulesImageFr);
    rulesFr.setTexture(rulesFrTex);
    rulesFr.setScale(2.5, 2.5);

    /* Chargement de images des règles du jeu de force */
    Image rulesImageFo;
    rulesImageFo.loadFromFile("Game1/Images/rulesFo.png");
    rulesFoTex.loadFromImage(rulesImageFo);
    rulesFo.setTexture(rulesFoTex);
    rulesFo.setScale(2.5, 2.5);

    /* Initialisation des attributs restants */
    onRules = true;
}


/**
 * Redimensionnement de l'image des règles des jeux
 *
 * @param window : Fenêtre de jeu
 */
void Rules::resizeRules(sf::RenderWindow &window){

    /* Coordonnées des règles en fonction de la taille de la fenêtre de jeu */
    Vector2u size = window.getSize();
    rulesFr.setPosition(size.x/3 - 8*48*size.y / (11*1536),
                      static_cast<float>(size.y / 3 - 10 * 48 * size.y / (11 * 1536)));

    rulesFo.setPosition(size.x/3 - 8*48*size.y / (11*1536),
                        static_cast<float>(size.y / 3 - 10 * 48 * size.y / (11 * 1536) - size.y*178/768));

    Vector2f rulesPosition = rulesFr.getPosition();

    /* Coordonnées du bouton 'OK' pour commencer à jouer */
    RulesItem btnOK;
    btnOK.rect.top= static_cast<int>(rulesPosition.y + 2.5*162*size.y / 1536);
    btnOK.rect.height = static_cast<int>(2.5*48*size.y / 1536);
    btnOK.rect.left = static_cast<int>(rulesPosition.x + 2.5*87*size.y / 1536);
    btnOK.rect.width = static_cast<int>(2.5*129*size.x / 2048);

    this->btnOK = btnOK;
}


/**
 * Dessiner les règles de jeu
 *
 * @param window : Fenêtre de jeu
 * @param game : Instance du jeu
 * @return
 */
void Rules::drawRules(RenderWindow &window, Game &game){
    game.render();

    /* Différentes règles en fonction du jeu : fréquence ou force */
    if (game.getGameType() == "fr") window.draw(rulesFr);
    else if (game.getGameType() == "fo") window.draw(rulesFo);

    /* Affichage de la fenêtre de jeu */
    window.display();

    return GetRulesResponse(window, game);
}


/**
 * Retourne l'action de jeu à exécuter selon l'endroit clické lorsque les règles de jeu sont affichées
 *
 * @param x Coordonnée selon l'axe des abscisse du click
 * @param y Coordonnée selon l'axe des ordonnées du click
 * @return
 */
bool Rules::HandleClick(int x, int y) {
    sf::Rect<int> rulesItemRect = btnOK.rect;
    return (rulesItemRect.top + rulesItemRect.height) > y
           && rulesItemRect.top < y
           && rulesItemRect.left < x
           && (rulesItemRect.left + rulesItemRect.width) > x;
}



/**
 * Traitement des évènements quand les règles de jeu sont affichées
 *
 * @param window : Fenêtre de jeu
 * @param input : Entrée à gérer
 * @param activityIsFinished : Booléen indiquant si l'activité est finie
 * @return : L'action a effectuée suite au click
 */
void Rules::GetRulesResponse(RenderWindow &window, Game &game) {
    sf::Event rulesEvent;
    Input *input = game.getInput();

    /* Boucle des événements */
    while(input->getActive() ? window.pollEvent(rulesEvent) : window.waitEvent(rulesEvent)) { // faire avec active cf input.cpp

        switch (rulesEvent.type) {

            /* Fenêtre fermé de façon inattendue */
            case sf::Event::Closed:
                mutex2.lock();
                input->setReadData(false);
                mutex2.unlock();
                input->getReader().wait();
                window.close();
                break;

            /* Gestion des cas où l'application perd le focus afin que l'application ne consomme pas trop de ressource si elle est situé en tâche de fond */
            case sf::Event::LostFocus:
                input->setActive(false);
                break;
            case sf::Event::GainedFocus:
                input->setActive(true);
                break;
            case sf::Event::MouseLeft:
                input->setActive(false);
                break;
            case sf::Event::MouseEntered:
                input->setActive(true);
                break;


            /* Si l'écran est touché... */
            case sf::Event::TouchBegan:

                /* Ne fonctionne pas mais normalement détecte le doigt, il doit s'agir de l'index pour que le contact soit compté */
                if (rulesEvent.touch.finger == 0){

                    /* Action à effectuer est récupérée en fonction des coordonnées du click */
                    bool ok = HandleClick(rulesEvent.touch.x, rulesEvent.touch.y);

                    /* Démarrage du jeu si le bouton 'OK' est clické */
                    if (ok){
                        onRules = false;
                        game.getPartyClock().restart();
                    }
                }
                break;

            default:
                break;
        }
    }
}

