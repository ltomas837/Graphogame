#ifndef PROJECT_SOUND_HPP
#define PROJECT_SOUND_HPP

#include <SFML/Audio.hpp>
#include <iostream>

using namespace sf;

/**
 * Classe représentant l'ensemble des sons associés au jeu
 */

class GameSound {
private:
    Music _musicBackground;
    Sound        _jumpSound;
    Sound        _scoreSound;
    Sound        _loseSound;
    SoundBuffer  _jump;
    SoundBuffer  _score;
    SoundBuffer  _lose;

public:

    /** Constructeur */
    GameSound();

    /** Les noms sont assez explicites */
    void playBackgroundMusic();
    void playJumpSound();
    void playScoreSound();
    void playLoseSound();
};

#endif
