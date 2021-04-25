package com.example.bangla_bondhu.ui.main;

/**
 * Author: Changemyminds.
 * Date: 2018/6/24.
 * Description:
 * Reference:
 */
public interface Speech {
    void start(String text);

    void resume();

    void pause();

    void stop();

    void exit();
}
