#include <Graphogame/Background.hpp>



Background::Background() {

  //if (!backgroundTex.loadFromFile("img/water4.jpg")){
  //if (!backgroundTex.loadFromFile("img/water3.png")){
  if (!backgroundTex.loadFromFile("Game2/Images/water.jpg")){
    std::cerr << "Background image is invalid" << std::endl;
    std::exit(EXIT_FAILURE);
  }
  backgroundTex.setSmooth(true);
  background.setTexture(backgroundTex);
  //background.setScale(2.5, 2);
}



void Background::drawBackground(RenderWindow &window) {
  window.draw(background);
}
