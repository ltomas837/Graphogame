#include <Graphogame/Game.hpp>


int main(){
    srand(time(NULL)); // Initialisation de l'aléatoire pour la génération des obstacles
    Game game;
    game.run(); // penser à faire un menu avant de run
    return EXIT_SUCCESS;
}
