#include <Graphogame/GameSound.hpp>

GameSound::GameSound() {

    if (!_musicBackground.openFromFile("Game2/Sounds/bgMusic.ogg")){
        std::cerr << "The background musics can't be loaded" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    _musicBackground.setLoop(true);

    // Chargement des sons
    if (!_swim.loadFromFile("Game2/Sounds/swim.ogg")){
        std::cerr << "Score sound can't be loaded" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    if (!_score.loadFromFile("Game2/Sounds/score.ogg")){
        std::cerr << "Score sound can't be loaded" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    if (!_lose.loadFromFile("Game2/Sounds/lose.ogg")){
        std::cerr << "Score sound can't be loaded" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    isSwimming = false;
    scoreSoundCurrentlyPlayed = true;
}



void GameSound::playBackgroundMusic(){
    _musicBackground.setVolume(50);
    _musicBackground.play();
}



void GameSound::playSwimSound(){
  if (!isSwimming){
    isSwimming = true;
    swimming.restart();
    _swimSound.setBuffer(_swim);
    _swimSound.play();
  }
  if (1000 < swimming.getElapsedTime().asMilliseconds()){
    isSwimming = false;
  }
}



void GameSound::playScoreSound(){
  _scoreSound.setBuffer(_score);
  _scoreSound.play();
}



void GameSound::playLoseSound(){
  _loseSound.setBuffer(_lose);
  _loseSound.play();
}



bool GameSound::getScoreSoundCurrentlyPlayed(){
  return scoreSoundCurrentlyPlayed;
}



void GameSound::setScoreSoundCurrentlyPlayed(bool scoreSoundCurrentlyPlayed){
  this->scoreSoundCurrentlyPlayed = scoreSoundCurrentlyPlayed;
}
