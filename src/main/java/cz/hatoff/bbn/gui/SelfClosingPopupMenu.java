package cz.hatoff.bbn.gui;

import com.alee.laf.menu.PopupMenuWay;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.managers.style.skin.web.PopupStyle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelfClosingPopupMenu extends WebPopupMenu implements ActionListener, ChangeListener {

    protected Timer timer;

    public SelfClosingPopupMenu() {
        super();
        this.timer = new Timer(2000, this);
        timer.setRepeats(false);
        MenuSelectionManager.defaultManager().addChangeListener(this);
        setPopupStyle(PopupStyle.simple);
    }

    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
    }

    public void actionPerformed(ActionEvent e) {
        setVisible(false);
    }

    public void stateChanged(ChangeEvent e) {
        if( MenuSelectionManager.defaultManager().getSelectedPath().length==1 ) {
            timer.start();
        } else {
            timer.stop();
        }
    }
}

