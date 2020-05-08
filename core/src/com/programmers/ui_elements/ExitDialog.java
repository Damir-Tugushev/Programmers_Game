package com.programmers.ui_elements;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.programmers.interfaces.Procedure;

public abstract class ExitDialog extends Dialog implements Procedure {

    public ExitDialog (String title, Skin skin) {
        super(title, skin);
        this.button("YES", true);
        this.button("NO", false);
        this.setMovable(false);
    }

    @Override
    protected void result(Object object) {
        if (object.equals(true)) {
            call();
        }
    }
}