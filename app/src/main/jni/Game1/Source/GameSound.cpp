#include <Graphogame/GameSound.hpp>



/**
 * Chargement de la musique de fond et des différents sons de jeu.
 *
 */
GameSound::GameSound() {

    /* Chargement de la musique de fond et la jouer en boucle lorsqu'elle est lancée */
    if (!_musicBackground.openFromFile("Game1/Sounds/bgMusic.ogg")){
        std::cerr << "The background musics can't be loaded" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    _musicBackground.setLoop(true);

    /* Chargement des sons de jeu */
    if (!_jump.loadFromFile("Game1/Sounds/jump.ogg")){
        std::cerr << "Score sound can't be loaded" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    if (!_score.loadFromFile("Game1/Sounds/score.ogg")){
        std::cerr << "Score sound can't be loaded" << std::endl;
        std::exit(EXIT_FAILURE);
    }
    if (!_lose.loadFromFile("Game1/Sounds/lose.ogg")){
        std::cerr << "Score sound can't be loaded" << std::endl;
        std::exit(EXIT_FAILURE);
    }

}

void GameSound::playBackgroundMusic(){
    _musicBackground.setVolume(50);
    _musicBackground.play();
}

void GameSound::playJumpSound(){
  _jumpSound.setBuffer(_jump);
  _jumpSound.play();
}

void GameSound::playScoreSound(){
  _scoreSound.setBuffer(_score);
  _scoreSound.play();
}

void GameSound::playLoseSound(){
  _loseSound.setBuffer(_lose);
  _loseSound.play();
}
