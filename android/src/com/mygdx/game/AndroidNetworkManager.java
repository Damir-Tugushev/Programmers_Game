package com.mygdx.game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.Gdx;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.programmers.enums.Difficulty;
import com.programmers.game.Player;
import com.programmers.game.hotseat.HotseatGameController;
import com.programmers.game.online.OnlineGameController;
import com.programmers.interfaces.Procedure;
import com.programmers.interfaces.NetworkManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class AndroidNetworkManager implements NetworkManager {

    private final static String rooms = "rooms";
    private final static String fieldData = "fieldData";
    private final static String gameData = "gameData";
    private final static String playersData = "playersData";
    private final static String cardsData = "cardsData";

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    public void registerAnon() {
        firebaseAuth.signInAnonymously().addOnSuccessListener(
                new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firebaseUser = authResult.getUser();
                        Gdx.app.log("USER_ID", firebaseUser.getUid());
                    }
                }
        );
        while (firebaseUser == null);
    }

    @Override
    public boolean createNewRoom(final String name, final int playersCount, final Difficulty difficulty) {
        final Boolean[] temp = { null };
        firebaseFirestore.collection(rooms).document(name).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            temp[0] = false;
                        } else {
                            temp[0] = true;
                            Room room = new Room(name, playersCount, difficulty);
                            room.getPlayers().add(firebaseUser.getUid());
                            firebaseFirestore.collection(rooms).document(name).set(room);
                        }
                    }
                });
        while (temp[0] == null);
        return temp[0];
    }

    @Override
    public void deleteRoom(String name) {
        firebaseFirestore.collection(rooms).document(name)
                .collection(gameData).document(playersData).delete();
        firebaseFirestore.collection(rooms).document(name)
                .collection(gameData).document(cardsData).delete();
        firebaseFirestore.collection(rooms).document(name)
                .collection(fieldData).document(fieldData).delete();
        firebaseFirestore.collection(rooms).document(name).delete();
    }

    @Override
    public void launchRoom(Room room) {
        room.setLaunched(true);
        firebaseFirestore.collection(rooms).document(room.getName()).set(room);
    }

    @Override
    public void setPlayersID(Room room) {
        final GameData.PlayersData[] playersData = { null };
        DocumentReference documentReference =
                firebaseFirestore.collection(rooms)
                        .document(room.getName())
                        .collection(gameData)
                        .document(AndroidNetworkManager.playersData);
        documentReference.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        playersData[0] = documentSnapshot.toObject(GameData.PlayersData.class);
                    }
                }
        );
        while (playersData[0] == null);
        Collections.shuffle(room.getPlayers());
        for (int i = 0; i < playersData[0].getPlayers().size(); i++) {
            GameData.Player player = playersData[0].getPlayers().get(i);
            player.setID(room.getPlayers().get(i));
        }
        documentReference.set(playersData[0]);
    }

    @Override
    public void toNextPlayer(Room room) {
        final GameData.PlayersData[] playersData = { null };
        DocumentReference documentReference =
                firebaseFirestore.collection(rooms)
                        .document(room.getName())
                        .collection(gameData)
                        .document(AndroidNetworkManager.playersData);
        documentReference.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        playersData[0] = documentSnapshot.toObject(GameData.PlayersData.class);
                    }
                }
        );
        while (playersData[0] == null);
        playersData[0].indexToNextPlayer();
        documentReference.set(playersData[0]);
    }

    @Override
    public boolean addPlayerToRoom(final Room room) {
        final Boolean[] temp = { null };
        firebaseFirestore.collection(rooms).document(room.getName())
                .get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            room.getPlayers().add(firebaseUser.getUid());
                            firebaseFirestore.collection(rooms)
                                    .document(room.getName()).set(room);
                            temp[0] = true;
                        } else {
                            temp[0] = false;
                        }
                    }
                });
        while (temp[0] == null);
        return temp[0];
    }

    @Override
    public boolean removePlayerFromRoom(final Room room) {
        final Boolean[] temp = { null };
        firebaseFirestore.collection(rooms).document(room.getName())
                .get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            room.getPlayers().remove(firebaseUser.getUid());
                            firebaseFirestore.collection(rooms)
                                    .document(room.getName()).set(room);
                            temp[0] = true;
                        } else {
                            temp[0] = false;
                        }
                    }
                });
        while (temp[0] == null);
        return temp[0];
    }

    @Override
    public LinkedList<Room> findRooms() {
        final boolean[] temp = { false };
        final LinkedList<Room> linkedList = new LinkedList<>();
        firebaseFirestore.collection(rooms).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                linkedList.add(documentSnapshot.toObject(Room.class));
                            }
                        }
                        temp[0] = true;
                    }
                });
        while (!temp[0]);
        return linkedList;
    }

    private ListenerRegistration listenerRegistration;

    @Override
    public void addRoomChangedListener(final Room room, final Procedure exists,
                                       final Procedure doesNotExists) {
        listenerRegistration = firebaseFirestore.collection(rooms)
                .document(room.getName())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e == null && documentSnapshot != null && documentSnapshot.exists()) {
                    Room newRoom = documentSnapshot.toObject(Room.class);
                    if (newRoom != null) {
                        room.set(newRoom);
                        exists.call();
                    } else {
                        doesNotExists.call();
                    }
                } else {
                    doesNotExists.call();
                }
            }
        });
    }

    @Override
    public void removeListener(Room room) {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    @Override
    public GameData.Player getThisPlayerData(Room room) {
        final GameData.Player[] player = { null };
        firebaseFirestore.collection(rooms).document(room.getName())
                .collection(gameData).document(playersData)
                .get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        GameData.PlayersData playersData =
                                documentSnapshot.toObject(GameData.PlayersData.class);
                        for (GameData.Player playerData : playersData.getPlayers()) {
                            if (firebaseUser.getUid().equals(playerData.getID())) {
                                player[0] = playerData;
                                break;
                            }
                        }
                    }
                }
        );
        while (player[0] == null);
        return player[0];
    }

    @Override
    public void sendFieldData(Room room, com.programmers.game.Field field) {
        FieldData fieldData = new FieldData(field);
        firebaseFirestore.collection(rooms).document(room.getName())
                .collection(AndroidNetworkManager.fieldData)
                .document(AndroidNetworkManager.fieldData)
                .set(fieldData);
    }

    @Override
    public FieldData getFieldData(Room room) {
        final FieldData[] fieldData = { null };
        firebaseFirestore.collection(rooms).document(room.getName())
                .collection(AndroidNetworkManager.fieldData)
                .document(AndroidNetworkManager.fieldData)
                .get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        fieldData[0] = documentSnapshot.toObject(FieldData.class);
                    }
                }
        );
        while (fieldData[0] == null);
        return fieldData[0];
    }

    @Override
    public void sendGameData(Room room, HotseatGameController hotseatGameController) {
        GameData gameData = new GameData(hotseatGameController);
        firebaseFirestore.collection(rooms)
                .document(room.getName())
                .collection(AndroidNetworkManager.gameData)
                .document(playersData).set(gameData.getPlayersData());
        firebaseFirestore.collection(rooms)
                .document(room.getName())
                .collection(AndroidNetworkManager.gameData)
                .document(cardsData).set(gameData.getCardsData());
    }

    @Override
    public void updateGameData(Room room, OnlineGameController onlineGameController, Player player) {
        //
    }

    @Override
    public GameData getGameData(Room room) {
        final GameData[] gameData = { null };
        final GameData.PlayersData[] playersData = { null };
        firebaseFirestore.collection(rooms).document(room.getName())
                .collection(AndroidNetworkManager.gameData)
                .document(AndroidNetworkManager.playersData)
                .get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        playersData[0] = documentSnapshot.toObject(GameData.PlayersData.class);
                    }
                }
        );
        while (playersData[0] == null);
        firebaseFirestore.collection(rooms).document(room.getName())
                .collection(AndroidNetworkManager.gameData)
                .document(cardsData)
                .get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        gameData[0] = new GameData(
                                playersData[0],
                                documentSnapshot.toObject(GameData.CardsData.class)
                        );
                    }
                }
        );
        while (gameData[0] == null);
        return gameData[0];
    }
}