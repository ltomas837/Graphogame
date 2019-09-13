#ifndef PROJECT_GAME_HPP
#define PROJECT_GAME_HPP

#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <SFML/OpenGL.hpp>
#include <iostream>
#include <cstdlib>
#include <string>
#include <ctime>

#include <iomanip>
#include <sstream>

#include <Graphogame/Input.hpp>
#include <Graphogame/Fence.hpp>
#include <Graphogame/Character.hpp>
#include <Graphogame/GameSound.hpp>
#include <Graphogame/Background.hpp>


using namespace sf;


class Game {
  private:
      RenderWindow  window;
      Input         input;
      Background    background;   // image de fond
      Clock         relativeClock; // sert à déterminer le temps relatif écoulé à chaque tour de jeu
      Clock         globalClock;  // sert à déterminer le temps de jeu qui s'est écoulé pour une partie de jeu entière
      bool          isStarted;    // si l'utilisateur a démarré le jeu --> espace pour se faire
      Fence         fences[12];   // les deux côtés de la grotte découpés en 11 rectangles (10 + 1 lorque le premier commence à disparaître)
      Texture       fenceUpTex;    // pas besoin de la recharger pour chaque pipe --> gain de performance important (cf doc)
      Texture       fenceDownTex;  // de même
      double        speed;        // vitesse de gravitation et de déplacement des tuyaux (en pixel/ms)
      Character     character;    // personnage de jeu
      Font          font;         // police pour le texte affiché par le jeu
      GameSound     gameSound;    // sons de jeu
      float         score;        // score de jeu
      //Menu          menu;


  public:
      Game();
      void update(Time);
      void render();
      void processEvents();
      void run();
      ~Game();
};

#endif
