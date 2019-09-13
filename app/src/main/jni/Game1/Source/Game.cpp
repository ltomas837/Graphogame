#include <Graphogame/Game.hpp>




Game::Game() {

    /* Chargement de l'image des obstacles */
    Image fenceUpImg;
    Image fenceDownImg;

    if (!fenceUpImg.loadFromFile("Game1/Images/fenceUp.png")){
        std::cerr << "Fence-up image is invalid" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    if (!fenceDownImg.loadFromFile("Game1/Images/fenceDown.png")){
        std::cerr << "Fence-down image is invalid" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    /* Rend invisible le contour des obstacles dont la couleur RGB est [80, 202, 222)]*/
    fenceUpImg.createMaskFromColor(Color(80, 202, 222));
    fenceDownImg.createMaskFromColor(Color(80, 202, 222));
    if (!fenceUpTex.loadFromImage(fenceUpImg)){
        std::cerr << "Fence-up image is invalid" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    if (!fenceDownTex.loadFromImage(fenceDownImg)){
        std::cerr << "Fence-down image is invalid" << std::endl;
        std::exit(EXIT_FAILURE);
    }

    /* Chargement de la police pour les écritures SFML (exemple: score) */
    if (!font.loadFromFile("Game1/Fonts/Xerox.ttf")){
        std::cerr << "Can't load the font" << std::endl;
        std::exit(EXIT_FAILURE);
    }

    /* Chargement de la fenêtre de jeu */
    std::vector<VideoMode> modes = VideoMode::getFullscreenModes(); // ne sera jamais vide car il y aura au pire toujours la résolution du bureau ----> retourner par VideoMode::getDesktopMode()
    window.create(modes[0], "Game", Style::Fullscreen);
    window.setVerticalSyncEnabled(true);

    /* Redimensionnement de l'image expliquant les règles */
    rules.resizeRules(window);

    /* Initialisation des attributs ne devant l'être qu'au début de l'activité */
    gameType = "";
    activityElapsedTime = 0;

    /* Initialisation des attributs de l'input pointant vers des attributs de l'instance de Game, voir Input.hpp */
    input.setActivityElapsedTime(&activityElapsedTime);
    input.setOnRules(rules.getOnRules());

    /* Chargement du type de jeu et de la difficulté envoyés par l'application et récupérés depuis la socket TCP par Input::reader */
    std::unique_lock<std::mutex> lckDifficulty(mutexDifficulty);
    input.setGameType(&gameType);
    input.setGameDifficulty(&difficulty);
    cv.notify_one();

    /* Chargement du temps restant de l'activité envoyé par l'application et récupéré depuis la socket TCP par Input::reader */
    std::unique_lock<std::mutex> lckRemainedTimed(mutexRemainedTime);
    input.setActivityRemainedTime(&remainedTime);
    cv.notify_one();
}


/**
 * Finition de l'initialisation du jeu. Fonction appelée à chaque essai.
 *
 */
void Game::initGame(){

    /* Redimensionnement du menu et de ses boutons */
    menu.resizeMenu(window);

    /* Redimensionnement de l'image de fond à la taille de l'écran */
    background.resizeBackground(window);

    /* Redimensionnement du personnage adaptée la taille de l'écran */
    character.resizeCharacter(window, background.getTexture());

    /* Initialisation des obstacles */
    Vector2f position;
    position.x = static_cast<float>(350 * window.getSize().x / 1366);
    position.y = 0;
    fences[0].initFence(window, fenceUpTex, fenceDownTex, fences[0], position, false);
    for (int i=1; i<5; i++){
        position.x += 350*window.getSize().x/1366; // Décalage du même écart selon l'axe des abscisses
        fences[i].initFence(window, fenceUpTex, fenceDownTex, fences[(i+4)%5], position, true);
    }

    /* Initialisation du reste des paramètres */
    isStarted = false;
    score = 0;
    speed = 0.1;
}


/**
 * Fonction principal contenant la boucle de jeu.
 *
 */
void Game::run(){

    /* On lance la récupération des données bluetooth des capteurs envoyés depuis l'application via la socket TCP */
    input.getReader().launch();

    /* On lance la musique de fond */
    gameSound.playBackgroundMusic();

    /* Affichage des règles jusqu'à ce que le joueur valide */
    while (*rules.getOnRules() && input.getReadData()){rules.drawRules(window, *this);}

    /* Horloge servant à mesurer le temps relativement à chaque boucle de jeu de la partie */
    Clock clock;

    /* Boucle principale du jeu */
    while (window.isOpen() && input.getReadData()){
        Time elapsed = clock.restart();

        /* Traitement des entrées */
        processEvents();

        /* Dessiner l'image à afficher */
        render();

        /* Affichage de la fenêtre de jeu */
        window.display();

        /* Mise à jour des éléments du jeu */
        update(elapsed);
    }

    /* Fermeture de la fenêtre de jeu si la socket TCP s'est fermé pour une raison inattendue (exemple : bluetooth désactivé en plein partie)*/
    if (window.isOpen())
        window.close();
}


/**
 * Traitement des évènements de jeu
 *
 */
void Game::processEvents(){
    input.inputManagement(window);
}


/**
 * Dessiner l'image à afficher (sans le menu ni les règles de jeu)
 *
 */
void Game::render(){

  Text text;

  /* Nettoyage de la fenêtre de jeu */
  window.clear();

  /* Dessiner l'image de fond */
  background.drawBackground(window);

  /* Dessiner les obstacles */
  for (int i=0; i<5; i++){
    fences[i].drawFence(window);
  }

  /* Dessiner le personnage */
  character.drawCharacter(window);

  /* Dessiner le score */
  Color color(0, 0, 0, 128);
  text.setFont(font);
  text.setString(std::to_string(score));
  text.setCharacterSize(window.getSize().x/10);
  text.setFillColor(color);
  text.setPosition(window.getSize().x/2, window.getSize().y/10);
  window.draw(text);

}



/**
 * Mise à jour des éléments du jeu (personnage, obstacles etc)
 *
 * @param elapsed : Temps écoulé pour cette boucle de jeu
 */
void Game::update(Time elapsed){

    /* Si un menu est affiché */
    if (*menu.getOnMenu()){
        Menu::MenuResult result;

        /* Mise à jour du temps écoulé pour cette essai, l'input l'enverra sur la socket à l'application pour mettre à jour l'activité dans la base de données, voir la supprimer si finie */
        input.setElapsedTime(partyClock.getElapsedTime().asMilliseconds());

        mutex2.lock();
        int readData = input.getReadData();
        mutex2.unlock();

        /* Tant que la socket n'a pas été fermée (de façon attendue ou inattendue) */
        while (readData){

            /* Dessiner le menu */
            result = menu.drawMenu(window, *this);

            /* On récupère le booléen indiquant s'il faut continuer à lire les données sur la socket, ou encore si la socket n'a pas été fermée */
            mutex2.lock();
            readData = input.getReadData();
            mutex2.unlock();

            /* Si le joueur a appuyé mais sur aucun bouton... */
            if (result == Menu::Nothing) {continue;}

            /* Si le joueur a appuy" sur rejouer */
            else if (result == Menu::Play) {

                /* Réinitialisation du jeu */
                initGame();

                /* On ne dessine plus le menu */
                menu.setOnMenu(false);
            }

            /* Enfin on quitte la boucle pour pouvoir refaire une partie */
            break;
        }
    }
    else {
        mutex1.lock();
        bool jump = input.getButton()->jump;
        mutex1.unlock();

        /* Si la partie de jeu n'est pas encore démarré et que le joueur demande à sauter */
        if (!isStarted) {
            if (jump) {
                /* Démarrer la partie */
                isStarted = true;
                partyClock.restart();
                elapsed = Time::Zero; // À ne pas enlever ! Le joueur ayant généralement passé un certain sur le menu, le temps écoulé depuis la dernière boucle de jeu est inapproprié et pose problème pour updateFence() si le joueur jump dès la première boucle de cette nouvelle partie
            }
        }

        /* Si le jeu est démarré.. Ne pas remplacer par else ! */
        /* Sinon problème pour updateCharacter car l'horloge Character::lastJump donne une durée inappropriée si le thread passant par le if ne passe pas jump pas à la boucle suivante, la trajectoire du personnage est alors faussée */
        if (isStarted) {

            /* Détection collisions */
            FloatRect CharacterBoundingBox = character.getCharacter().getGlobalBounds();
            FloatRect fenceBoundingBox;
            for (int i = 0; i < 5; i++) {

                /* Détection collison avec l'obstacle du haut */
                fenceBoundingBox = fences[i].getFenceUp().getGlobalBounds();
                if (CharacterBoundingBox.intersects(fenceBoundingBox)) {
                    gameSound.playLoseSound();
                    std::this_thread::sleep_for(std::chrono::milliseconds(1000));

                    /* Affichage du menu */
                    menu.setOnMenu(true);
                    return;
                }

                /* Détection collison avec l'obstacle du bas */
                fenceBoundingBox = fences[i].getFenceDown().getGlobalBounds();
                if (CharacterBoundingBox.intersects(fenceBoundingBox)) {
                    gameSound.playLoseSound();
                    std::this_thread::sleep_for(std::chrono::milliseconds(1000));

                    /* Affichage du menu */
                    menu.setOnMenu(true);
                    return;
                }
            }

            /* Dépassement du personnage en bas de la vue */
            if (770 * window.getSize().y / 768 < character.getCharacter().getPosition().y) {
                gameSound.playLoseSound();
                std::this_thread::sleep_for(std::chrono::milliseconds(1000));

                /* Affichage du menu */
                menu.setOnMenu(true);
                return;
            }

            /* Mise à jour de la position des obstacles */
            for (int i = 0; i < 5; i++) {
                fences[i].updateFence(window, fences[(i + 4) % 5], gameSound, &score, speed,
                                      elapsed);
            }

            /* Mise à jour de la position du personnage*/
            character.updateCharacter(window, gameSound, jump);
        }
    }
}
