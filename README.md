# Graphogame

Projet **Graphogame** développé sous l'IDE *Android Studio*.

### Prérequis

Afin de pouvoir lancer le projet sous [Android Studio](https://developer.android.com/docs), plusieurs installations sont nécessaires.
Les jeux lancés par l'application sont développés avec la [SFML](https://www.sfml-dev.org/).

Ce [lien Github](https://github.com/MoVoDesign/SFML_Template) explique de façon détaillé comment utiliser la *SFML* sous *Android Studio* sous *Ubuntu*. Suivez la section 'Build SFML' et créez les librairies *SFML* pour *armeabi-v7a* (la plupart des tablettes fonctionnent de nos jours avec ce processeur. Néanmoins, il serait intéressant d'éteindre  le champ de validité de l'application, par exemple aux dernières tablettes fonctionnant sous *armeabi-v8a*).
Plusieurs points sont à prendre en compte afin d'importer le projet dans *Android Studio* avec succès.

* Téléchargez la version 12b du *Native Development Kit* comme indiqué sur le lien ci-dessus : **NDK-r12b**. Une fois dans *Android studio*, allez dans 
```
File->Project Stucture...->SDK Location
```
Puis sélectionnez le répertoire contenant le **NDK-r12b** que vous venez de télécharger pour le chemin **Android NDK location**.

* Construire l'*APK* sur un appareil *Android* de version supérieur ou égale à *5.0* (testée sur *7.0*, tablette *Samsung* modèle *SM-T813*, néanmoins spécifiquement pour ce modèle, la fonctionnalité "vibration" fournie avec l'example *Android* de la *SFML* par exemple lors d'une collision n'est pas disponible). De plus, l'appareil *Android* doit fonctionner sous *armeabi-v7a* puisque vous venez de les construire pour ce processeur (de même pour les jeux, mais possibilité d'éteindre à tous processeurs).

Essayez tout d'abord de lancer l'exemple *Android* de la *SFML*, si vous y arrivez alors c'est exactement la même chose pour ce projet. La localisation de l'exemple de la *SFML* est *SFML/examples/android/*. Afin d'ouvrir un projet existant dans *Android studio*, allez dans 
```
File->Open...
```
Puis sélectionnez la localisation du projet.

Pour toute question, vous pouvez me contacter sur mon adresse email : **ltomas@enseirb-matmeca.fr**.
