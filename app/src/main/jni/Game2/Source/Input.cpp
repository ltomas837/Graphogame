#include <Graphogame/Input.hpp>



Input::Input() {
    button.up = false;
    button.down = false;
    button.jump = false;
}



Input::Button Input::getButton() const {
    return button;
}



void Input::inputManagement(RenderWindow &window){
    // Boucle des événements
    while (window.pollEvent(event)) {
        // Gestion des différents cas d'événements
        switch (event.type) {
            // Si on ferme la fenêtre
            case Event::Closed:
                window.close();
                break;

            // Si on appuie sur une touche du clavier
            case Event::KeyPressed:
                // On regarde sur quelle touche on a appuyé
                switch (event.key.code) {
                  case Keyboard::Space:
                      button.jump = true;
                      break;

                    case Keyboard::Up:
                        button.up = true;
                        break;

                    case Keyboard::Down:
                        button.down = true;
                        break;

                    default:
                        break;
                }
                break;

            // Si on relâche une touche du clavier
            case Event::KeyReleased:
                // On regarde quelle touche a été relâchée
                switch (event.key.code) {
                    case Keyboard::Space:
                        button.jump = false;
                        break;

                    case Keyboard::Up:
                        button.up = false;
                        break;

                    case Keyboard::Down:
                        button.down = false;
                        break;

                    default:
                        break;
                }
                break;
                // Pas d'autre événement
            default:
                break;
        }
    }
}

/*
Input::Input() {
    button.left = false;
    button.right = false;
    button.up = false;
    button.down = false;
    button.jump = false;
    button.enter = false;
}



void Input::inputManagement(RenderWindow &window){
    // Boucle des événements
    while (window.pollEvent(event)) {
        // Gestion des différents cas d'événements
        switch (event.type) {
            // Si on ferme la fenêtre
            case Event::Closed:
                window.close();
                break;

                // Si on appuie sur une touche du clavier
            case Event::KeyPressed:
                // On regarde sur quelle touche on a appuyé
                switch (event.key.code) {
                    case Keyboard::Space:
                        button.jump = true;
                        break;

                    case Keyboard::Up:
                        button.up = true;
                        break;

                    case Keyboard::Down:
                        button.down = true;
                        break;

                    case Keyboard::Left:
                        button.left = true;
                        break;

                    case Keyboard::Right:
                        button.right = true;
                        break;

                    case Keyboard::Return:
                        button.enter = true;
                        break;

                    default:
                        break;
                }
                break;

                // Si on relâche une touche du clavier
            case Event::KeyReleased:
                // On regarde quelle touche a été relâchée
                switch (event.key.code) {
                    case Keyboard::Space:
                        button.jump = false;
                        break;

                    case Keyboard::Up:
                        button.up = false;
                        break;

                    case Keyboard::Down:
                        button.down = false;
                        break;

                    case Keyboard::Left:
                        button.left = false;
                        break;

                    case Keyboard::Right:
                        button.right = false;
                        break;

                    case Keyboard::Return:
                        button.enter = false;
                        break;

                    default:
                        break;
                }
                break;
                // Pas d'autre événement
            default:
                break;
        }
    }
}
*/
