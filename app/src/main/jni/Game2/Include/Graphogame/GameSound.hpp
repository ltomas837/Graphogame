#ifndef PROJECT_SOUND_HPP
#define PROJECT_SOUND_HPP

#include <SFML/Audio.hpp>
#include <iostream>

using namespace sf;

class GameSound {
private:
    Clock        swimming;
    bool         isSwimming;
    bool         scoreSoundCurrentlyPlayed;
    Music        _musicBackground;
    Sound        _swimSound;
    Sound        _scoreSound;
    Sound        _loseSound;
    SoundBuffer  _swim;
    SoundBuffer  _score;
    SoundBuffer  _lose;

public:
    GameSound();

    // Getter
    bool getScoreSoundCurrentlyPlayed();

    // Setter
    void setScoreSoundCurrentlyPlayed(bool);

    // Les noms sont assez explicites
    void playBackgroundMusic();

    void playSwimSound();

    void playScoreSound();

    void playLoseSound();
};

#endif
