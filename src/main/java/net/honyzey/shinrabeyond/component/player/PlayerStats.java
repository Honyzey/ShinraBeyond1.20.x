package net.honyzey.shinrabeyond.component.player;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface PlayerStats extends Component {

    int getMana();
    void setMana(int value);

    int getForce();
    void setForce(int value);

    void initIfNeeded();

}
