package com.programmers.ui_elements;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.programmers.enums.CardType;
import com.programmers.enums.Difficulty;
import com.programmers.game.GameCard;
import com.programmers.game.GameController;

public class CardContainer extends Table {

    public static final Array<CardContainer> cardContainers = new Array<>(3);
    private final Content content;
    private final Card emptyCard;
    private final Difficulty difficulty;
    private final GameController gameController;

    private int prevChildrenCount;
    public boolean discardMode = false;

    public CardContainer(final Array<GameCard> gameCards, final Difficulty difficulty,
                         final Content content, GameController gameController) {
        this.content = content;
        this.difficulty = difficulty;
        this.gameController = gameController;
        setDebug(true);

        emptyCard = new Card(
                "Sprites/EnabledCards/empty.png",
                gameController.getGameScreen().getAssetManager()
        );
        if (content != null)
            addEmpty();
        if (gameCards != null) {
            for (GameCard gameCard : gameCards) {
                Card card = new Card(
                        gameCard, gameController.getGameScreen().getAssetManager()
                );
                addCard(card, 0, 0);
            }
        }
        prevChildrenCount = getChildren().size;
        cardContainers.add(this);
        //setDebug(true);
    }

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
        controlEmpty();
        AlgorithmCardWindow window = gameController.getAlgorithmCardWindow();
        if (window != null) {
            CycleCardContainer cycleCardContainer = (CycleCardContainer) window.getCyclesCardContainer();
            if (difficulty == Difficulty.Hard && content == Content.Actions)
                cycleCardContainer.drawPoints(prevChildrenCount, getChildren().size);
        }

        prevChildrenCount = getChildren().size;
        setTouchable();
    }

    void setActionToPrevious() {
        for (int i = 0; i < getChildren().size; i++) {
            Card card = (Card) getChild(i);
            if (card != null)
                card.setActionToPrevious(this);
        }
    }

    void controlEmpty() {
        if (content != null) {
            if (getChildren().size > 1)
                removeEmpty();
            else if (getChildren().isEmpty())
                addEmpty();
        }
    }

    void removeEmpty() {
        Cell cell = getCell(emptyCard);
        emptyCard.remove();
        getCells().removeValue(cell, true);
        invalidate();
    }

    protected void setTouchable() {
        if (!discardMode) {
            if (getChildren().size < 3)
                setTouchable(Touchable.disabled);
            else
                setTouchable(Touchable.enabled);
        }
    }

    void addEmpty() {
        add(emptyCard).row();
    }

    public void addCard(final Card card, final float globalX, final float globalY) {
        switch (content) {
            case Actions:
                if (card.getGameCard().getCardType() != CardType.Cycle2
                        && card.getGameCard().getCardType() != CardType.Cycle3) {
                    if (card.getCell() == null)
                        add(card).row();
                    else
                        card.getCell().setActor(card);
                } else {
                    if (card.getCell() == null)
                        card.getPrevParent().add(card).row();
                    else {
                        card.getCell().setActor(card);
                        ((CycleCardContainer) card.getPrevParent()).removeSpace(
                                card, card.getPrevParent().getCells()
                        );
                    }
                }
                break;
            case All:
                add(card).row();
                card.setCell(null);
        }
    }

    @Override
    public void clearChildren() {
        super.clearChildren();
        controlEmpty();
    }

    GameController getGameController() {
        return gameController;
    }

    public enum Content {
        Actions,
        All
    }
}
