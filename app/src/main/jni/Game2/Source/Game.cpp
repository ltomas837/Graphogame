#include <Graphogame/Game.hpp>

#include <thread>
#include <chrono>



Game::Game(){

  // Chargement de l'image des obstacles
  Image fenceUpImg;
  Image fenceDownImg;

  // Chargement de la fenêtre
  std::vector<VideoMode> modes = VideoMode::getFullscreenModes(); // ne sera jamais vide car il y aura au pire toujours la résolution du bureau ----> retourner par VideoMode::getDesktopMode()
  window.create(modes[0], "Game", Style::Fullscreen);
  window.setVerticalSyncEnabled(true);

  if (!fenceUpTex.loadFromFile("Game2/Images/rockUp.png")){//, sf::IntRect(0, 0, 512, 867))){
      std::cerr << "Fence-up image is invalid" << std::endl;
      std::exit(EXIT_FAILURE);
  }
  if (!fenceDownTex.loadFromFile("Game2/Images/rockDown.png")){//, sf::IntRect(0, 0, window.getSize().x/10, 867))){
      std::cerr << "Fence-down image is invalid" << std::endl;
      std::exit(EXIT_FAILURE);
  }

  // Chargement de la police
  if (!font.loadFromFile("Game2/Fonts/Xerox.ttf")){
    std::cerr << "Can't load the font" << std::endl;
    std::exit(EXIT_FAILURE);
  }

  // Initialisation des obstacles
  Vector2f position;
  position.x = 0;
  position.y = 0;
  fences[0].initFence(fenceUpTex, fenceDownTex, fences[0], position, false);
  for (int i=1; i<12; i++){
    position.x += window.getSize().x/10;
    fences[i].initFence(fenceUpTex, fenceDownTex, fences[(i+11)%12], position, true);
  }

  // Initialisation du reste des paramètres
  speed = 0.02*5; //*5
  isStarted = false;
  score = 0;
}



Game::~Game() {}



void Game::run(){

  // On lance la musique de fond
  gameSound.playBackgroundMusic();

  // Boucle principale du jeu --> à modifier plus tard (mettre booléen comme sur la doc pour save les données si fenêtre fermée)
  while (window.isOpen()){
    processEvents();
    render();
    Time elapsed = relativeClock.restart();
    update(elapsed);
  }
}



void Game::processEvents(){
  input.inputManagement(window);
}



void Game::render(){

  Text text;
  window.clear();

  // Affichage du background
  background.drawBackground(window);

  // Affichage des obstacles
  for (int i=0; i<12; i++){
    fences[i].drawFence(window);
  }
  character.drawCharacter(window);

  // Affichage du score
  Color color(0, 0, 0, 128);
  text.setFont(font);
  std::stringstream stream;
  stream << std::fixed << std::setprecision(3) << score;
  text.setString(stream.str());
  text.setCharacterSize(100);
  text.setFillColor(color);
  text.setPosition(660, 100);
  window.draw(text);

  window.display();
}



void Game::update(Time elapsed){

    if (!isStarted){
      if (input.getButton().jump){
        isStarted = true;
        globalClock.restart();
      }
    }

    if (isStarted){

      // Détection collisions
      FloatRect boundingBox = character.getCharacter().getGlobalBounds();
      FloatRect upBoundingBox;
      FloatRect downBoundingBox;
      for (int i=0; i<12; i++){
        upBoundingBox = fences[i].getFenceUp().getGlobalBounds();
        downBoundingBox = fences[i].getFenceDown().getGlobalBounds();
        if (boundingBox.intersects(upBoundingBox) || boundingBox.intersects(downBoundingBox)){
          gameSound.playLoseSound();
          std::this_thread::sleep_for(std::chrono::milliseconds(1000));
          //menu.setOnMenu(true);
          //return;
          window.close(); // à changer, possibilité de rejouer
          exit(EXIT_SUCCESS);
        }
      }


      // Mise à jour de la position des obstacles et du personnage
      for (int i=0; i<12; i++){
        fences[i].updateFence(window, fences[(i+11)%12], gameSound, &score, speed, elapsed);
      }
      character.updateCharacter(input, gameSound, elapsed);
    }

}
