package net.honyzey.shinrabeyond.component.player;

import dev.onyxstudios.cca.api.v3.component.Component;

/**
 * Interface publique du composant de stats joueur.
 * - get/set pour les stats
 * - initIfNeeded() pour la génération au premier join
 * - rerollStats() pour reroll par commande/item
 * - destin/archétype exposé (string)
 * - training / cap modifiers exposés pour usage gameplay
 */
public interface PlayerStats extends Component {
    int getMana();
    void setMana(int value);

    int getForce();
    void setForce(int value);

    boolean isInitialized();
    void setInitialized(boolean v);

    void initIfNeeded(); // call côté serveur à la première connexion

    void rerollStats();

    // Archétype / destiny
    String getDestiny();
    void setDestiny(String destiny);

    double getTrainingModifier();
    double getMaxStatModifier();
}
