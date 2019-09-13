#include <Graphogame/Game.hpp>



/**
 * La SFML permet d'utiliser un main() pour servir de point d'entrée du jeu.
 *
 */
int main(int argc, char *argv[]){

    /* Initialisation de l'aléatoire pour la génération des obstacles */
    srand(static_cast<unsigned int>(time(NULL)));

    /* Initialisation du jeu */
    Game game;
    game.initGame();

    /* Démarrage du jeu */
    game.run();

    return EXIT_SUCCESS;
}


