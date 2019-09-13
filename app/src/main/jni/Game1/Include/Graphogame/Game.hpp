#ifndef PROJECT_GAME_HPP
#define PROJECT_GAME_HPP

#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <SFML/OpenGL.hpp>

#include <Graphogame/Menu.hpp>
#include <Graphogame/Input.hpp>
#include <Graphogame/Fence.hpp>
#include <Graphogame/Character.hpp>
#include <Graphogame/GameSound.hpp>
#include <Graphogame/Background.hpp>
#include <Graphogame/Rules.hpp>

#include <iostream>
#include <cstdlib>
#include <string>
#include <ctime>
#include <thread>
#include <chrono>


static std::mutex mutexDifficulty;
static std::mutex mutexRemainedTime;
static sf::Mutex mutex1;
static sf::Mutex mutex2;
static sf::Mutex mutex3;
static std::condition_variable cv;


using namespace sf;


/**
 * Classe représentant le jeu en lui-même
 *
 */
class Game {

  private:

      /** Fnêtre de jeu */
      RenderWindow  window;

      /** Entrées */
      Input         input;

      /** Image de fond */
      Background    background;

      /** Horloge mesurant le temps qui s'est écoulé au cours d'un essai */
      Clock         partyClock;

      /** Booléen indiquant si l'utilisateur a démarré l'essai  */
      bool          isStarted;

      /** Les obstacles */
      Fence         fences[5];

      /** Texture, pour ne pas à avoir à chaque obstacle, gain de performance selon la documentation, sûrement par pour 5 ?... Pas eu le temps de changer */
      Texture       fenceUpTex;

      /** Texture, pour ne pas à avoir à chaque obstacle, gain de performance selon la documentation, sûrement par pour 5 ?... Pas eu le temps de changer */
      Texture       fenceDownTex;

      /** Vitesse de défilement des obstacles (en pixel/ms) */
      double        speed;

      /** Personnage de jeu*/
      Character     character;

      /** Police pour les écritures SFML (exemple: score) */
      Font          font;


      /** Sons de jeu */
      GameSound     gameSound;

      /** Score de jeu */
      int           score;

      /** Menus de jeux (paire)*/
      Menu          menu;

      /** Diificulté de l'activité */
      int           difficulty;

      /** Type de jeu (fréquence/force) */
      std::string   gameType;

      /** Règles de jeu */
      Rules         rules;

      /** Temps restant de l'activité */
      int           remainedTime;

      /** Temps écoulé depuis le début de l'activité */
      int           activityElapsedTime;



  public:

      /** Constructeur */
      Game();

      /** Getters */
      Input *getInput() {return &input;}
      Clock &getPartyClock() {return partyClock;}
      int getRemainedTime() {return remainedTime;}
      int getActivityElapsedTime() {return activityElapsedTime;}
      std::string getGameType() {return gameType;}

      /** Destructeur */
      ~Game() {}

      /** Finition de l'initialisation du jeu. Fonction appelée à chaque essai.*/
      void initGame();

      /** Mise à jour des éléments du jeu (personnage, obstacles etc) */
      void update(Time);

      /** Dessiner l'image à afficher (sans le menu ni les règles de jeu) */
      void render();

      /** Traitement des évènements de jeu */
      void processEvents();

      /** Fonction principal contenant la boucle de jeu. */
      void run();
};


#endif
