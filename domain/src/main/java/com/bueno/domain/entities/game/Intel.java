/*
 *  Copyright (C) 2021 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.bueno.domain.entities.game;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.player.util.Player;

import java.time.Instant;
import java.util.*;

public class Intel{

    private final Instant timestamp;

    private boolean gameIsDone;
    private UUID gameWinner;
    private boolean maoDeOnze;

    private HandScore handScore;
    private HandScore handScoreProposal;
    private int maximumHandScore;
    private List<Optional<String>> roundWinners;
    private int roundsPlayed;

    private Player currentPlayer;
    private int currentPlayerScore;
    private String currentPlayerUsername;
    private int currentOpponentScore;
    private String currentOpponentUsername;
    private Card vira;
    private Card cardToPlayAgainst;
    private List<Card> openCards;
    private List<Card> currentPlayerCards;
    private EnumSet<PossibleActions> possibleActions;
    private HandResult handResult;

    public static Intel of(Hand currentHand){
        Hand hand = Objects.requireNonNull(currentHand);
        Intel result = new Intel();
        result.setHandIntel(hand);
        result.setPlayersIntel(hand);
        return result;
    }

    public static Intel ofConcluded(Game concludedGame){
        Game game = Objects.requireNonNull(concludedGame);
        Intel result = of(game.currentHand());
        result.setGameIntel(game);
        return result;
    }

    private void setGameIntel(Game game){
        gameIsDone = getGameResult(game);
        gameWinner = getGameWinner(game);
    }
    private void setHandIntel(Hand hand){
        maoDeOnze = hand.isMaoDeOnze();
        handScore = hand.getScore();
        handScoreProposal = hand.getScoreProposal();
        maximumHandScore = hand.getMaxHandScore();
        roundWinners = getRoundWinners(hand);
        roundsPlayed = roundWinners.size();
        vira = hand.getVira();
        handResult = hand.getResult().orElse(null);
        openCards = List.copyOf(hand.getOpenCards());
        cardToPlayAgainst = hand.getCardToPlayAgainst().orElse(null);
        possibleActions = EnumSet.copyOf(hand.getPossibleActions());
    }

    private void setPlayersIntel(Hand hand){
        currentPlayer = hand.getCurrentPlayer();
        currentPlayerCards = currentPlayer != null ? List.copyOf(currentPlayer.getCards()) : null;
        currentPlayerScore = currentPlayer != null ?  currentPlayer.getScore() : 0;
        currentPlayerUsername = currentPlayer != null ?  currentPlayer.getUsername() : null;
        currentOpponentScore = currentPlayer != null ?  hand.getOpponentOf(currentPlayer).getScore() : 0;
        currentOpponentUsername = currentPlayer != null ?  hand.getOpponentOf(currentPlayer).getUsername() : null;
    }

    private Intel() {
        timestamp = Instant.now();
    }

    private boolean getGameResult(Game game) {
        return game.getPlayer1().getScore() == Player.MAX_SCORE || game.getPlayer1().getScore() == Player.MAX_SCORE;
    }

    private UUID getGameWinner(Game game) {
        if (game.getPlayer1().getScore() == Player.MAX_SCORE) return game.getPlayer1().getUuid();
        if (game.getPlayer2().getScore() == Player.MAX_SCORE) return game.getPlayer2().getUuid();
        return null;
    }

    private List<Optional<String>> getRoundWinners(Hand hand) {
        return hand.getRoundsPlayed().stream()
                .map(Round::getWinner)
                .map(maybeWinner -> maybeWinner.orElse(null))
                .map(player -> player != null ? player.getUsername() : null)
                .map(Optional::ofNullable)
                .toList();
    }

    public Instant timestamp() {
        return timestamp;
    }

    public boolean isGameDone() {
        return gameIsDone;
    }

    public Optional<UUID> gameWinner() {
        return Optional.ofNullable(gameWinner);
    }

    public boolean isMaoDeOnze() {
        return maoDeOnze;
    }

    public HandScore handScore() {
        return handScore;
    }

    public HandScore scoreProposal() {
        return handScoreProposal;
    }

    public int maximumHandScore() {
        return maximumHandScore;
    }

    public List<Optional<String>> roundWinners() {
        return roundWinners;
    }

    public int roundsPlayed() {
        return roundsPlayed;
    }

    public Card vira() {
        return vira;
    }

    public Optional<HandResult> handResult() {
        return Optional.ofNullable(handResult);
    }

    public List<Card> openCards() {
        return openCards;
    }

    public Optional<Card> cardToPlayAgainst() {
        return Optional.ofNullable(cardToPlayAgainst);
    }

    public EnumSet<PossibleActions> possibleActions() {
        return possibleActions;
    }

    public Optional<UUID> currentPlayerUuid() {
        return currentPlayer != null ? Optional.of(currentPlayer.getUuid()) : Optional.empty();
    }

    public List<Card> currentPlayerCards(){return new ArrayList<>(currentPlayerCards);}

    public int currentPlayerScore() {
        return currentPlayerScore;
    }

    public String currentPlayerUsername() {
        return currentPlayerUsername;
    }

    public int currentOpponentScore() {
        return currentOpponentScore;
    }

    public String currentOpponentUsername() {
        return currentOpponentUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intel intel = (Intel) o;
        return timestamp.equals(intel.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return "[" + timestamp +
                " ] Current player = " + currentPlayerUsername +
                " | Vira = " + vira +
                " | Card to play against = " + cardToPlayAgainst +
                " | Open cards = " + openCards +
                " | Possible actions = " + possibleActions +
                " | Rounds = " + roundWinners +
                " | Result = " + handResult;
    }
}