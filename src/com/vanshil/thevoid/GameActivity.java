package com.vanshil.thevoid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;

public class GameActivity extends GraphicsActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
View.OnClickListener, RealTimeMessageReceivedListener,
RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener {

	private static final int RC_SIGN_IN = 0, RC_INVITATION_INBOX = 2, RC_SELECT_PLAYERS = 3, RC_WAITING_ROOM = 4;

	// Request codes for the UIs that we show with startActivityForResult:
	final static String TAG = "THE VOID";
	private GoogleApiClient client;// Client used to interact with Google APIs.
	GameView gameView;

	// Message buffer for sending messages
	byte[] mMsgBuf = new byte[2];

	private boolean playingGame = false, resolutionInProgress;

	String roomId = null;

	UiView uiView;

	private void checkForFail(int statusCode){
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//enables screen to sleep again.
		}
	}

	private void checkIfInvited(Bundle connectionHint){
		if (connectionHint != null) {
			Log.d(GameActivity.TAG, "onConnected: connection hint provided. Checking for invite.");
			final Invitation invitation = connectionHint
					.getParcelable(Multiplayer.EXTRA_INVITATION);
			if (invitation != null && invitation.getInvitationId() != null){
				joinInvitedRoom(invitation);
				return;
			}
		}
	}
	public void endGame(){
		leaveRoom();
		running = false;
		startUi();
		running = true;
	}

	private void invitationReceivedResult(int response, Intent data){
		if (response != Activity.RESULT_OK) {
			// canceled
			return;
		}

		joinInvitedRoom((Invitation)(data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION)));

		// prevent screen from sleeping during handshake
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void inviteFriendsResult(int response, Intent data){
		if (response != Activity.RESULT_OK) {
			// user canceled
			return;
		}

		// get the invitee list
		//Bundle extras = data.getExtras();
		final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

		// create the room and specify a variant if appropriate
		final RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
		roomConfigBuilder.addPlayersToInvite(invitees);

		final RoomConfig roomConfig = roomConfigBuilder.build();
		Games.RealTimeMultiplayer.create(client, roomConfig);

		// prevent screen from sleeping during handshake
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}
	private void joinInvitedRoom(Invitation invitation){
		Log.d(GameActivity.TAG, "Accepting invitation: " + invitation.getInvitationId());
		final RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setInvitationIdToAccept(invitation.getInvitationId())
		.setMessageReceivedListener(this)
		.setRoomStatusUpdateListener(this);
		Games.RealTimeMultiplayer.join(client, roomConfigBuilder.build());

	}
	private void leaveRoom(){
		Games.RealTimeMultiplayer.leave(client, this, roomId);
	}

	private RoomConfig.Builder makeBasicRoomConfigBuilder() {
		return RoomConfig.builder(this)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(this);
	}
	
	@Override
	public void onActivityResult(int request, int response, Intent data) {
		if (request == GameActivity.RC_SELECT_PLAYERS){
			inviteFriendsResult(response, data);
		}
		else if(request == GameActivity.RC_WAITING_ROOM){
			roomResult(response);
		}else if (request == GameActivity.RC_INVITATION_INBOX){
			invitationReceivedResult(response, data);
		}
	}
	@Override
	public void onClick(View v) {

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d("CONNECTION", "SUCCESSFULL");
		Games.Invitations.registerInvitationListener(client, this);
		checkIfInvited(connectionHint);
	}
	@Override
	public void onConnectedToRoom(Room room) {
		roomId = room.getRoomId();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("CONNECTION", "FAILED" + result.getErrorCode());
		if (!resolutionInProgress) {
			try {
				result.startResolutionForResult(this, GameActivity.RC_SIGN_IN);
				resolutionInProgress = true;
				Log.d("CONNECTION", "TRIED");
			} catch (final SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				resolutionInProgress = false;
				client.connect();
			}
		}
	}
	@Override
	public void onConnectionSuspended(int arg0) {
		client.connect();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create the Google Api Client with access to Plus and Games
		client = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
		.addApi(Games.API).addScope(Games.SCOPE_GAMES)
		.build();

		startUi();
		//Log.d(TAG, getString(R.string.app_id));
	}
	@Override
	public void onDisconnectedFromRoom(Room arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onInvitationReceived(Invitation invitation) {
		Log.d("INVITATION", "RECIEVED");
		if(!playingGame){
			final Intent intent = Games.Invitations.getInvitationInboxIntent(client);
			startActivityForResult(intent, GameActivity.RC_INVITATION_INBOX);
		}
	}
	@Override
	public void onInvitationRemoved(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		checkForFail(statusCode);
		Log.d("ROOM", "JOINED");
		roomId = room.getRoomId();
		showRoomActivity(room);
	}

	@Override
	public void onLeftRoom(int arg0, String arg1){
	}

	@Override
	public void onP2PConnected(String arg0) {

	}
	@Override
	public void onP2PDisconnected(String arg0) {
		leaveRoom();
	}
	@Override
	protected void onPause(){
		super.onPause();
	}


	@Override
	public void onPeerDeclined(Room arg0, List<String> arg1) {
		leaveRoom();
	}

	@Override
	public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {

	}

	@Override
	public void onPeerJoined(Room arg0, List<String> arg1) {

	}

	@Override
	public void onPeerLeft(Room arg0, List<String> arg1) {
		leaveRoom();
	}
	@Override
	public void onPeersConnected(Room arg0, List<String> arg1) {

	}

	@Override
	public void onPeersDisconnected(Room arg0, List<String> arg1) {
		leaveRoom();
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		if(playingGame){
			gameView.messageIn(rtm.getMessageData());
		}
	}
	@Override
	protected void onResume(){
		super.onResume();
	}

	@Override
	public void onRoomAutoMatching(Room arg0) {
		
	}

	@Override
	public void onRoomConnected(int statusCode, Room room) {
		checkForFail(statusCode);

	}
	@Override
	public void onRoomConnecting(Room arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onRoomCreated(int statusCode, Room room) {
		checkForFail(statusCode);
		Log.d("ROOM", "CREATED");
		roomId = room.getRoomId();
		showRoomActivity(room);
	}

	@Override
	protected void onStart(){
		if (client != null && client.isConnected()) {
			Log.w(GameActivity.TAG, "GameHelper: client was already connected on onStart()");
		}
		else {
			Log.d(GameActivity.TAG, "Connecting client.");
			client.connect();
		}
		super.onStart();
	}
	@Override
	protected void onStop() {
		super.onStop();
		client.disconnect();
	}
	private void roomResult(int response){
		// we got the result from the "waiting room" UI.
		if (response == Activity.RESULT_OK) {
			// ready to start playing
			Log.d(GameActivity.TAG, "Starting game (waiting room returned OK).");
			startGame();
		} else if (response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
			// player indicated that they want to leave the room
			leaveRoom();
		} else if (response == Activity.RESULT_CANCELED) {
			// Dialog was cancelled (user pressed back key, for instance). In our game,
			// this means leaving the room too. In more elaborate games, this could mean
			// something else (like minimizing the waiting room UI).
			leaveRoom();
		}
		//break;
	}
	private void sendMessage(){
		if(client.isConnected() && playingGame){
			final byte[] message1 = gameView.messageOutUnits();
			final byte[] message2 = gameView.messageOutArrows();
			if(message1.length>1){
				Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(client, message1, roomId);
				Log.d("MESSAGE", "Message 1 SENT");
			}
			if(message2[0] == 102){
				Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(client, message2, roomId);
				Log.d("MESSAGE", "Message 2 SENT");
			}
		}
	}
	private void showRoomActivity(Room room){
		// get waiting room intent
		final Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(client, room, Integer.MAX_VALUE);
		startActivityForResult(i, GameActivity.RC_WAITING_ROOM);
	}
	public void startFriendPicker(){
		if(client.isConnected()){
			final Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(client, 1, 3);
			startActivityForResult(intent, GameActivity.RC_SELECT_PLAYERS);
		}

	}
	public void startGame(){
		playingGame = true;
		gameView = new GameView(this);
		setView(gameView);
	}
	public void startInstructions(){
		final InstructionView iView = new InstructionView(this);
		setView(iView);
	}
	public void startQuickGame(){
		final Bundle autoMatch = RoomConfig.createAutoMatchCriteria(1, 1, 0);

		// build the room config:
		final RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
		roomConfigBuilder.setAutoMatchCriteria(autoMatch);
		final RoomConfig roomConfig = roomConfigBuilder.build();

		// create room:
		Games.RealTimeMultiplayer.create(client, roomConfig);

		// prevent screen from sleeping during handshake
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		startGame();
	}
	public void startUi(){
		playingGame = false;
		uiView = new UiView(this);
		setView(uiView);
	}

	@Override
	public void update(Canvas canvas, GraphicsView graphicsView){
		super.update(canvas, graphicsView);
		if(current.init){
			sendMessage();
			//Log.d("MESSAGE GEN", gameView.updateIn(gameView.updateOut()));
		}
	}
}
