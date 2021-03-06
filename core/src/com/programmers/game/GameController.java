package com.programmers.game;

import com.badlogic.gdx.utils.Array;
import com.programmers.enums.Difficulty;
import com.programmers.screens.GameScreen;
import com.programmers.ui_elements.AlgorithmCardWindow;
import com.programmers.ui_elements.CardContainer;
import com.programmers.ui_elements.PlayerCardWindow;

import java.util.Stack;

import static com.badlogic.gdx.math.MathUtils.random;

public abstract class GameController {

    protected Player thisPlayer;
    protected PlayerCardWindow playerCardWindow;
    protected AlgorithmCardWindow algorithmCardWindow;
    protected final Field field;

    protected final Array<GameCard> algorithmToDo = new Array<>();
    protected final Array<GameCard> discardPile = new Array<>(52);
    protected final Stack<GameCard> talon = new Stack<>();

    protected final GameScreen gameScreen;

    protected GameController(GameScreen gameScreen, Field field) {
        this.gameScreen = gameScreen;
        this.field = field;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public AlgorithmCardWindow getAlgorithmCardWindow() {
        return algorithmCardWindow;
    }

    public Array<GameCard> getAlgorithmToDo() {
        return algorithmToDo;
    }

    public Array<GameCard> getDiscardPile() {
        return discardPile;
    }

    public Stack<GameCard> getTalon() {
        return talon;
    }

    public PlayerCardWindow getPlayerCardWindow() {
        return playerCardWindow;
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }

    public abstract void toNextPlayer();

    protected void initContainers() {
        CardContainer playerCardContainer = new CardContainer(
                thisPlayer.getGameCards(),
                getDifficulty(), CardContainer.Content.All,
                this
        );
        playerCardWindow = new PlayerCardWindow(
                "  Player cards  ", playerCardContainer,
                this, gameScreen.getAssetManager()
        );
        algorithmCardWindow = new AlgorithmCardWindow("  Algorithm  ",
                this, gameScreen.getAssetManager());
    }

    public abstract Difficulty getDifficulty();

    protected void makeTalon() {
        while (!discardPile.isEmpty()) {
            final int i = random.nextInt(discardPile.size);
            final GameCard gameCard = discardPile.get(i);
            talon.push(gameCard);
            gameCard.setPlayer(null);
            discardPile.removeIndex(i);
        }
    }
}
