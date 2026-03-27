# WebRTC Thread Calling Plan

This note captures the first practical WebRTC feature set for SchoolBridge, with a strong focus on message-thread-based calling so the product value and portfolio value line up.

## Priority Use Cases

Everything starts inside an existing message thread.

### 1. Parent-Teacher Voice/Video Calls
- A parent opens a conversation with a teacher.
- They tap `Call` or `Video call`.
- A WebRTC session is created and linked to that conversation.
- The other participant receives an incoming call UI.
- When the call ends, a call event remains in the thread.

### 2. Verification Calls For Role Requests
- A school admin reviews a role request.
- If identity or documents need clarification, the admin opens or continues the related message thread.
- The admin starts a quick verification call from that thread.
- The requester joins directly from the thread and the call log stays attached to the same conversation context.

### 3. Quick Audio/Video Escalation From Finance Or Discipline Conversations
- A finance or discipline conversation includes an urgent issue.
- Either side escalates the thread into a call instead of switching apps.
- The call event remains visible in the same thread for audit and continuity.

### 4. Announcements
- A school admin starts a live announcement session from an announcement thread.
- Phase 1 should keep this simple as `audio-first` or `host video + listeners`.
- This can reuse the same signaling and call event model later.

## Later Backlog

Keep these for later so the first rollout stays focused:

- school admin interview calls
- live tutoring or office hours between student and teacher
- small live class sessions
- screen sharing for homework help, especially for tablet/desktop

## Smallest Useful Feature To Ship First

Ship this first:

- 1:1 audio call from a message thread
- optional camera upgrade inside the same call
- call invite, ringing, accept, reject, cancel
- mute microphone
- end call
- call state persisted in backend
- call event saved in thread after end/miss/reject

Why this is the best first slice:

- technically real WebRTC, not a fake demo
- useful in product immediately
- reuses existing messaging
- works as the foundation for parent-teacher, verification, and finance/discipline escalation
- easier than starting with group calls or screen sharing

After that:

1. add video toggle
2. add incoming call notifications via FCM
3. add announcement mode

## Product Rules

### Core Rule
- Calls must always belong to a `conversationId`.

That keeps context clean and lets us:
- show the call history in the thread
- relate verification calls to role requests
- relate urgent calls to finance/discipline issues
- avoid inventing a separate disconnected calling product

### Call Permissions
- Only conversation participants can start or join a thread call.
- Verification calls should only be allowed in request-related threads where the admin and requester are both participants.
- Announcement calls should only be startable by users with the right admin role.

## Backend Design

Spring Boot should own signaling authorization, call state, persistence, and audit.
Media should still flow peer-to-peer where possible through WebRTC.

### Signaling Endpoints

Use WebSocket for live signaling and REST for persistence/history.

#### REST

`POST /api/conversations/{conversationId}/calls`
- Creates a call session
- Request:
  - `type`: `AUDIO` or `VIDEO`
  - `purpose`: `GENERAL`, `ROLE_VERIFICATION`, `FINANCE_ESCALATION`, `DISCIPLINE_ESCALATION`, `ANNOUNCEMENT`
  - `linkedRequestId`: optional role request id
  - `linkedMessageId`: optional triggering message id
- Response:
  - `callId`
  - `conversationId`
  - `status`
  - `createdAt`

`GET /api/conversations/{conversationId}/calls`
- Lists previous call sessions for the thread

`GET /api/calls/{callId}`
- Returns current call state

`POST /api/calls/{callId}/accept`
- Marks call as accepted by current user

`POST /api/calls/{callId}/reject`
- Marks call as rejected by current user

`POST /api/calls/{callId}/end`
- Ends call for everyone

`POST /api/calls/{callId}/ice-candidates`
- Optional REST fallback if WebSocket is not yet ready

#### WebSocket

`/ws/signaling`

Events:
- `call.invite`
- `call.cancelled`
- `call.accepted`
- `call.rejected`
- `call.ended`
- `webrtc.offer`
- `webrtc.answer`
- `webrtc.ice_candidate`
- `call.participant_state`

Payload fields should always include:
- `callId`
- `conversationId`
- `fromUserId`
- `toUserId` or `participantIds`
- timestamp

### Recommended Tables

#### `conversation_calls`
- `id`
- `conversation_id`
- `started_by_user_id`
- `call_type` (`AUDIO`, `VIDEO`)
- `purpose`
- `status` (`RINGING`, `ONGOING`, `MISSED`, `REJECTED`, `ENDED`, `CANCELLED`)
- `linked_role_request_id` nullable
- `linked_message_id` nullable
- `started_at`
- `answered_at` nullable
- `ended_at` nullable
- `ended_by_user_id` nullable
- `duration_seconds` nullable

#### `conversation_call_participants`
- `id`
- `call_id`
- `user_id`
- `role_in_call` (`CALLER`, `CALLEE`, `HOST`, `LISTENER`)
- `state` (`INVITED`, `RINGING`, `JOINED`, `REJECTED`, `MISSED`, `LEFT`)
- `joined_at` nullable
- `left_at` nullable

#### `conversation_call_events`
- `id`
- `call_id`
- `conversation_id`
- `event_type`
- `actor_user_id` nullable
- `payload_json`
- `created_at`

#### Optional later: `conversation_call_recordings`
- not needed for first ship

### Existing Thread Integration

When a call is created or ends, save a normal system message into the thread:

- `Missed audio call`
- `Video call started`
- `Verification call ended · 04:13`

That way call history is visible even before a dedicated call-history UI exists.

## Android Architecture

### New Layers

#### Data
- `ThreadCallApiService`
- `ThreadCallWebSocketService`
- `ThreadCallRepository`

#### Domain/UI models
- `ThreadCallUiModel`
- `IncomingCallUiModel`
- `CallParticipantUiModel`
- `CallPurpose`
- `CallType`
- `CallStatus`

#### ViewModels
- `ThreadCallViewModel`
- can be scoped to `MessageThreadScreen`

### Screen Structure

#### Inside `MessageThreadScreen`
- Add top actions:
  - `Audio`
  - `Video`
- Show call event bubbles in the thread timeline
- Show incoming call banner if thread has an active ringing call

#### New Call Screen

`ThreadCallScreen`

States:
- `Outgoing`
- `Incoming`
- `Connecting`
- `In Call`
- `Ended`

Controls:
- mute/unmute
- speaker on/off
- camera on/off
- switch front/back camera
- end call

For verification calls:
- show a small pill like `Verification Call`

For finance/discipline escalations:
- show a pill like `Urgent Finance` or `Urgent Discipline`

### Suggested Android Package Shape

`app/src/main/java/com/schoolbridge/v2/ui/call/`
- `ThreadCallScreen.kt`
- `IncomingCallSheet.kt`
- `CallControlsRow.kt`
- `CallStatusBanner.kt`

`app/src/main/java/com/schoolbridge/v2/data/remote/`
- `ThreadCallApiService.kt`
- `ThreadCallSignalSocket.kt`

`app/src/main/java/com/schoolbridge/v2/data/dto/call/`
- call DTOs

`app/src/main/java/com/schoolbridge/v2/data/repository/`
- `ThreadCallRepositoryImpl.kt`

## Android UI Flow

### Outgoing Call
1. User taps `Audio` in thread
2. App calls `POST /calls`
3. App opens `ThreadCallScreen`
4. WebSocket sends `call.invite`
5. Other user sees incoming call UI

### Incoming Call
1. WebSocket receives `call.invite`
2. App shows incoming call banner/sheet
3. User taps accept or reject
4. Accept sends signaling answer flow
5. Reject updates backend and thread event

### Verification Call Flow
1. Admin opens role request thread
2. Admin presses `Start verification call`
3. Call purpose is `ROLE_VERIFICATION`
4. Thread shows the resulting call event

## Data Models

### Backend enums

`CallType`
- `AUDIO`
- `VIDEO`

`CallPurpose`
- `GENERAL`
- `ROLE_VERIFICATION`
- `FINANCE_ESCALATION`
- `DISCIPLINE_ESCALATION`
- `ANNOUNCEMENT`

`CallStatus`
- `RINGING`
- `ONGOING`
- `MISSED`
- `REJECTED`
- `ENDED`
- `CANCELLED`

`ParticipantState`
- `INVITED`
- `RINGING`
- `JOINED`
- `REJECTED`
- `MISSED`
- `LEFT`

### Android DTOs

`CreateThreadCallRequest`
- `type`
- `purpose`
- `linkedRequestId`
- `linkedMessageId`

`ThreadCallResponse`
- `id`
- `conversationId`
- `type`
- `purpose`
- `status`
- `startedByUserId`
- `startedAt`
- `answeredAt`
- `endedAt`

`SignalEnvelope`
- `eventType`
- `callId`
- `conversationId`
- `fromUserId`
- `toUserId`
- `payload`

## WebRTC Technical Notes

### First Transport Setup
- STUN first for development
- TURN before production

Recommended first setup:
- Google STUN for local testing
- Coturn server for real deployment

### Signaling Choice
- Use WebSocket, not polling
- REST remains for persistence and history

### Library Direction For Android
- Native WebRTC Android SDK
- keep wrapper code small
- avoid abstracting too early

## Suggested First Implementation Order

### Phase 1
- create backend call entities and REST endpoints
- create WebSocket signaling channel
- add 1:1 audio calls inside message threads
- save call events back into thread

### Phase 2
- add video toggle
- add verification call purpose
- add finance/discipline escalation entry points

### Phase 3
- add announcement mode
- add FCM incoming call notifications
- add availability states and richer admin controls

## Thread Entry Points

Add call entry buttons in these places first:

- message thread header
- role request thread actions: `Start verification call`
- urgent finance message card: `Call now`
- urgent discipline message card: `Call guardian`

Do not add WebRTC entry points everywhere at once.

## Portfolio Framing

If you ship just the first slice well, this project already demonstrates:

- WebRTC signaling design
- real-time mobile state handling
- role-aware call permissions
- thread-integrated communications
- backend persistence for call audit/history

That is much stronger than just saying “video call support.”
