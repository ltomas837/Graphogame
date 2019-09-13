#include <Graphogame/Input.hpp>
#include <stdio.h>



/**
 * Gestion des entrées reçues lorsque l'utilisateur joue (c'est-à-dire lorsqu'il n'est pas sur un menu ou les règles de jeu)
 *
 * @param window : Fenêtre de jeu
 */
void Input::inputManagement(RenderWindow &window){

    /* Boucle des événements */
    while (active ? window.pollEvent(event) : window.waitEvent(event)) {

        switch (event.type){

            /* Fenêtre fermé de façon inattendue */
            case sf::Event::Closed:
                mutex2.lock();
                setReadData(false);
                mutex2.unlock();
                reader.wait();
                window.close();

            /* Gestion des cas où l'application perd le focus afin que l'application ne consomme pas trop de ressource si elle est situé en tâche de fond */
            case sf::Event::LostFocus:
                active = false;
                break;
            case sf::Event::GainedFocus:
                active = true;
                break;
            case sf::Event::MouseLeft:
                active = false;
                break;
            case sf::Event::MouseEntered:
                active = true;
                break;
            default:
                break;

        }
        /* Si on veut faire sauter le personnage lorsque l'écran est touché */
        //button.jump = sf::Touch::isDown(0);
    }
}


/**
 * Lire un mot sur la socket TCP
 *
 * @param their_sock : La socket client (vers l'application)
 * @param rfds : Ensemble des 'file descriptors'  à traiter, constitué d'un unique élément (la socket client), est nécessaire pour select()
 * @param buffer : La zone mémoire tampon à remplir
 * @return Le nombre de bits lus si tout va bien, le code d'erreur sinon
 */
int Input::getWord(int their_sock, fd_set rfds, char *buffer){

    int retval;
    strcpy(buffer, "");

    /* Initialisation du temps à bloquer pour select() */
    struct timeval tv;
    tv.tv_sec = 1;
    tv.tv_usec = 0;

    /* Utilisation non-bloquante de la socket. En effet si elle est détruite par l'application java, alors le jeu terminera après le timeout */
    if ((retval = select(their_sock+1, &rfds, NULL, NULL, &tv)) == -1) {
        mutex2.lock();
        readData = false;
        mutex2.unlock();
        LOGE("socket: %s\n", strerror(errno));
    }
    else if (!retval) { // Timeout expiré : on termine le thread et le jeu avec
        mutex2.lock();
        readData = false;
        mutex2.unlock();
    }
    /* Si la socket est prête en lecture, si la socket n'a pas été détruite en faite car une seconde d'attente, lire sur la socket */
    else if ((retval = read(their_sock, buffer, 256)) < 1) {
        mutex2.lock();
        readData = false;
        mutex2.unlock();
        LOGE("socket: %s\n", strerror(errno));
    }
    return retval;
}


/**
 * Écrire un mot sur la socket TCP
 *
 * @param their_sock : La socket client (vers l'application)
 * @param rfds : Ensemble des 'file descriptors' à traiter, constitué d'un unique élément (la socket client), est nécessaire pour select()
 * @param buffer : La zone mémoire tampon à lire
 * @return Le nombre de bits écrits si tout va bien, le code d'erreur sinon
 */
int Input::writeWord(int their_sock, fd_set rfds, char *buffer){

    int retval;
    size_t len = strlen(buffer);

    /* Initialisation du temps à bloquer pour select() */
    struct timeval tv;
    tv.tv_sec = 1;
    tv.tv_usec = 0;

    /* Utilisation non-bloquante de la socket. En effet si elle est détruite par l'application java, alors le jeu terminera après le timeout */
    if ((retval = select(their_sock+1, NULL, &rfds, NULL, &tv)) == -1) {
        mutex2.lock();
        readData = false;
        mutex2.unlock();
        LOGE("socket: %s\n", strerror(errno));
    }
    else if (!retval) { // Timeout expiré : on termine le thread et le jeu avec
        mutex2.lock();
        readData = false;
        mutex2.unlock();
    }
    /* Si la socket est prête en écriture, si la socket n'a pas été détruite en faite car une seconde d'attente, lire sur la socket */
    else if ((retval = write(their_sock, buffer, len)) < 1) {
        mutex2.lock();
        readData = false;
        mutex2.unlock();
        LOGE("socket: %s\n", strerror(errno));
    }

    strcpy(buffer, "");
    return retval;
}



/**
 * Fonction exécutée par le sf::Thread Input::reader
 *
 */
void Input::readBTData() {

    struct sockaddr_in my_addr;
    char buffer[256];
    int my_sock, their_sock;

    /* Initialisation de la socket TCP côté serveur */
    my_sock = socket(AF_INET, SOCK_STREAM, 0);

    /* Initialisation des caractéristiques de la socket */
    int option = 1;
    setsockopt(my_sock, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option));
    my_addr.sin_family = AF_INET;
    my_addr.sin_port = htons(8080); // numéro de port
    my_addr.sin_addr.s_addr = inet_addr("127.0.0.1"); // localhost

    /* Lier la socket à la structure précédemment définie */
    if(bind(my_sock,(struct sockaddr *)&my_addr,sizeof(my_addr)) != 0) {
        LOGE("socket: %s", strerror(errno));
        return;
    }

    /* Indiquer que le rôle de la socket est d'accepter des connexions entrantes (socket serveur) */
    if(listen(my_sock, 1) != 0) {
        LOGE("socket: %s", strerror(errno));
        return;
    }

    /* Écouter jusqu'à obtenir une connexion entrante */
    if((their_sock = accept(my_sock, NULL, NULL)) < 0) {
        LOGE("socket: %s", strerror(errno));
        return;
    }

    /* Définition de l'ensemble des 'file descriptors' à traiter, constitué d'un unique élément (la socket client), est nécessaire pour select() */
    fd_set rfds;
    FD_ZERO(&rfds);
    FD_SET(their_sock, &rfds);

    strcpy(buffer, "");


    /* Récupèrer la difficulté et le type de jeu sur la socket */

    /* Attend que gameDifficulty ait été initialisé.. attente passive */
    std::unique_lock<std::mutex> lckDifficulty(mutexDifficulty);
    while (!gameDifficulty) cv.wait(lckDifficulty);

    getWord(their_sock, rfds, buffer);

    std::istringstream isDifficulty(std::string(buffer).substr(2, 3));
    *gameType = std::string(buffer).substr(0, 2);
    isDifficulty >> *gameDifficulty;
    //LOGI("Difficulty: %d\n", *gameDifficulty);
    //LOGI("GameType: %s\n", gameType->c_str());


    /* Récupèrer le temps restant de l'activité en millisecondes */

    /* Certaines fois le temps restant est récupéré avec le ligne cryptant la difficulté et le type de jeu, d'autre fois non */
    char *newline = strchr(buffer, '\n');
    newline++;
    char *newline2 = strchr(newline, '\n');
    mutex3.lock();
    /* S'ils sont dans le même mot */
    if (newline2){
        newline2[0] = '\0';
        std::istringstream isRemainedTimed(newline);
        isRemainedTimed >> *remainedTime;
    }
    /* Sinon lire un nouveau mot sur la socket */
    else {
        getWord(their_sock, rfds, buffer);
        std::istringstream isRemainedTimed(buffer);
        isRemainedTimed >> *remainedTime;
    }
    mutex3.unlock();
    //LOGI("remainingTime: %d\n", *remainedTime);


    ///////////////////////////////////////////////////////////////////

    /* Données à envoyer sur la socket */
    char toSend[256];
    strcpy(toSend, "");

    /* Valeur des capteurs */
    int valp = 0;
    int valm = 0;

    /* Seuils de force auxquels faire sauter le personnage */
    int thresholdP = 0;
    int thresholdM = 0;

    mutex2.lock();
    bool tmpReadData = readData;
    mutex2.unlock();

    /* Tant qu'on doit lire les données venant de l'application sur la socket */
    while (tmpReadData) {

        /* Lire un mot sur la socket */
        getWord(their_sock, rfds, buffer);

        /* Si on ne lit rien, c'est que la socket est fermé du côté de l'application (par exemple par plus de bluetooth), donc fermer l'application */
        if (buffer[0] == '\0'){
            break;
        }

        /* Récupéré seulement la première ligne du mot lu */
        newline = strchr(buffer, '\n');
        newline[0] = '\0';
        std::string word(buffer);
        //LOGI("socket : %d\n", buffer);

        if (word != ""){
            /* S'il s'agit du capteur de force situé sur le pouce */
            if (word.substr(0, 2) == "fp") {
                /* On récupère la valeur du capteur */
                std::istringstream value(word.substr(2));
                value >> valp;

                /* Si les règles de jeu sont toujours affichées et qu'il s'agit du jeu de force, alors l'utilisateur est en train de définir le seuil de force, il est donc mis à jour */
                if ((*gameType == "fo") && onRules && (thresholdP < valp)) thresholdP = valp;
            }
            /* Sinon s'il s'agit du capteur de force situé sur le majeur... */
            else if (word.substr(0, 2) == "fm"){
                std::istringstream value(word.substr(2));
                value >> valm;
                if ((*gameType == "fo") && onRules && (thresholdP < valm)) thresholdM = valm;
            }

        }

        /* Le personnage est en train de sauter si les valeurs des deux capteurs sont supérieur à 80% du seuil de force */
        mutex1.lock();
        button.jump = ((int) thresholdP*80/100 < valp) && ((int) thresholdM*80/100 < valm);
        mutex1.unlock();

        /* elapsedTie est mise à jour à chaque fin de partie par le menu */
        if (elapsedTime){
            /* Mettre à jour le temps passé à jouer */
            mutex3.lock();
            *activityElapsedTime += elapsedTime;
            mutex3.unlock();

            /* Envoyer le temps passer sur l'essai sur la socket pour que l'application mette l'activité à jour dans la base de données */
            sprintf(toSend, "%d\n", elapsedTime);
            writeWord(their_sock, rfds, toSend);

            elapsedTime = 0;
        }

        mutex2.lock();
        tmpReadData = readData;
        mutex2.unlock();

    }

    /* Fermeture des sockets */
    close(my_sock);
    close(their_sock);

}








