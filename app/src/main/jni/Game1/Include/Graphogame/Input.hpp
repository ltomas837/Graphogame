#ifndef INPUT_HPP
#define INPUT_HPP


#include <SFML/System/NativeActivity.hpp>
#include <SFML/Graphics.hpp>

#include <android/native_window.h>
#include <android/looper.h>
#include <android/log.h>
#include <android/native_activity.h>


#include <sstream>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <sys/select.h>
#include <sys/time.h>
#include <unistd.h>




#include <errno.h>


/* Maccros de débugage */
static const char* kTAG = "ServerIPC";
#define LOGI(...) \
  ((void)__android_log_print(ANDROID_LOG_INFO, kTAG, __VA_ARGS__))
#define LOGE(...) \
  ((void)__android_log_print(ANDROID_LOG_ERROR, kTAG, __VA_ARGS__))



using namespace sf;



/**
 * Classe représentant les entrées reçues et traitées par le jeu
 *
 */
class Input {

  private:
      struct Button {
          bool jump;
      };

      /** Objet définissant l'ensemble des actions possible par l'utilisateur, unique action pour ce jeu */
      Button button;

      /** Évènement à traiter */
      Event  event;

      /** Booléen indiquant si l'application est en premier plan */
      bool   active;

      /** Thread lisant les données venant de l'application sur la socket TCP */
      sf::Thread reader;

      /** Booléen indiquant si 'reader' doit continuer à lire les données */
      bool readData;

      /** Temps écoulé pour le dernier essai */
      int elapsedTime;

      /** Pointeurs vers des attributs d'autres objets devant être traités par le thread 'reader' */
      int *gameDifficulty;
      int *remainedTime;
      int *activityElapsedTime;
      bool *onRules;
      std::string *gameType;

      /** Lit un mot sur la socket TCP */
      int getWord(int, fd_set, char *);

      /** Écrit un mot sur la socket TCP */
      int writeWord(int, fd_set, char *);

      /** Fonction exécutée par 'reader' */
      void readBTData();

  public:

      /** Constructeur */
      Input(): onRules(nullptr), gameType(nullptr), activityElapsedTime(nullptr), elapsedTime(0), readData(true), gameDifficulty(nullptr), remainedTime(nullptr), reader(&Input::readBTData, this){button.jump = false;}

      /** Getters */
      Input::Button *getButton(){return &button;}
      bool getActive(){return active;}
      sf::Thread &getReader(){return reader;}
      bool getReadData(){return readData;}

      /** Setters */
      void setActive(bool active) { this->active = active; }
      void setReadData(bool readData) { this->readData = readData; }
      void setGameDifficulty(int *difficulty) { gameDifficulty = difficulty; }
      void setActivityRemainedTime(int *remainedTime) {this->remainedTime = remainedTime;}
      void setElapsedTime(int elapsedTime) {this->elapsedTime = elapsedTime;}
      void setActivityElapsedTime(int *activityElapsedTime) {this->activityElapsedTime = activityElapsedTime;}
      void setOnRules(bool *onRules) {this->onRules = onRules;}
      void setGameType(std::string *gameType) {this->gameType = gameType;}

      /** Gestion des entrées lorsque l'utilisateur joue (c'est-à-dire lorsqu'il n'est pas sur un menu ou les règles de jeu) */
      void inputManagement(RenderWindow &);
};

#include <Graphogame/Game.hpp>

#endif
