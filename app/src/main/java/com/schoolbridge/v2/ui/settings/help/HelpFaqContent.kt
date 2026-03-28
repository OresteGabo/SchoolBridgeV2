package com.schoolbridge.v2.ui.settings.help

fun helpFaqContent(): List<FAQCategory> {
    return listOf(
        FAQCategory(
            "Roles & access", listOf(
                FAQItem(
                    question = "Can I request a role I already have?",
                    answer = "Yes. If you already have parent, teacher, student, or school admin access, you can still request another link or assignment. For example, a parent can request another child link, and a teacher can request access for another school.",
                    tags = listOf("role", "request", "parent", "teacher", "student", "admin", "link")
                ),
                FAQItem(
                    question = "How do role requests work now?",
                    answer = "Role requests start from a lookup flow. You search for the school or student, select the correct record, then submit the request with the required ID behind the scenes. This helps schools review the right person or institution.",
                    tags = listOf("role", "request", "search", "school", "student", "lookup", "id")
                ),
                FAQItem(
                    question = "Can more than one adult be linked to a child?",
                    answer = "Yes. A school can approve more than one trusted adult for the same child, such as another parent, guardian, relative, or family friend, depending on the school’s policy.",
                    tags = listOf("parent", "guardian", "trusted adult", "child", "link")
                ),
                FAQItem(
                    question = "Where do admins handle pending requests?",
                    answer = "The home screen only shows a summary. The actual approval flow is moving into communication threads, so admins can request documents, track uploads, and approve or reject requests in one place.",
                    tags = listOf("admin", "pending", "request", "thread", "documents", "approval")
                ),
                FAQItem(
                    question = "What happens when a school asks for more information?",
                    answer = "You will see that request in the related SchoolBridge thread. That thread becomes the place to reply, send clarification, upload requested documents, or follow a verification step.",
                    tags = listOf("more info", "documents", "request", "thread", "verification")
                ),
                FAQItem(
                    question = "Can I cancel a pending role request?",
                    answer = "That management flow is planned from the role area. For now, if the school has already started reviewing it, use the related thread or contact the school before submitting a new request.",
                    tags = listOf("cancel", "pending", "role request", "school")
                )
            )
        ),
        FAQCategory(
            "Messages, requests & calls", listOf(
                FAQItem(
                    question = "Why are SchoolBridge messages not like WhatsApp chats?",
                    answer = "Most SchoolBridge conversations are topic-based threads, not open personal chats. A thread can represent a school notice, a finance follow-up, a role request, a verification request, or an action that needs a response.",
                    tags = listOf("messages", "threads", "chat", "whatsapp", "schoolbridge")
                ),
                FAQItem(
                    question = "Who sees action buttons in a thread?",
                    answer = "Only the person who is expected to act should see the action buttons. If you are the sender who initiated the request, the thread can show status such as awaiting reply instead of asking you to respond to your own request.",
                    tags = listOf("actions", "thread", "buttons", "reply", "status")
                ),
                FAQItem(
                    question = "Why do some threads not show a text field?",
                    answer = "SchoolBridge is not designed as an open chat app. Many threads are action-based or read-only, so the expected reply may be a button choice, an acknowledgement, a document upload, or a call join instead of free typing. Free text is usually reserved for the school side or for specific conversation threads.",
                    tags = listOf("text field", "reply", "permissions", "thread", "action")
                ),
                FAQItem(
                    question = "What happens after I respond to an action request?",
                    answer = "Once the expected reply is sent, the thread should move out of action mode for that person. Instead of leaving the same input controls open forever, SchoolBridge can switch back to status guidance such as awaiting review, updated, or waiting for the other side.",
                    tags = listOf("action required", "reply", "status", "thread", "response")
                ),
                FAQItem(
                    question = "Can a thread expect different answer types?",
                    answer = "Yes. A school thread may expect a button response, a text note, a supporting document, a payment confirmation, or a call join depending on the workflow. The reply area should match what the thread is asking for, not always show the same composer.",
                    tags = listOf("answer type", "text", "documents", "buttons", "call", "workflow")
                ),
                FAQItem(
                    question = "Can a school invite me into a call from a thread?",
                    answer = "Yes. SchoolBridge is designed around invited calls inside a thread, such as a discipline follow-up, a finance escalation, a verification call, or a planned meeting. It is not meant to be a free-call app.",
                    tags = listOf("call", "invite", "thread", "meeting", "verification", "discipline")
                ),
                FAQItem(
                    question = "Will actions update live when both users are online?",
                    answer = "Yes. The messaging flow now supports real-time in-app updates through WebSockets. If someone confirms, accepts, rejects, or uploads what was requested, the other side can see the thread update without waiting for a manual refresh.",
                    tags = listOf("realtime", "websocket", "live", "actions", "thread")
                ),
                FAQItem(
                    question = "Why are some alerts now visible through messages?",
                    answer = "Many notices now fit better as system or school threads because they can carry links, updates, acknowledgements, countdowns, attachments, or follow-up steps. This keeps the full history in one place.",
                    tags = listOf("alerts", "messages", "system", "announcements", "threads")
                )
            )
        ),
        FAQCategory(
            "Finance & receipts", listOf(
                FAQItem(
                    question = "Who should see the finance tab?",
                    answer = "Finance is mainly for students, parents or guardians with linked children, and roles that need to follow school billing. If finance is not relevant to a teacher-only account, the app can surface a more useful work area such as schedule or teaching tools instead.",
                    tags = listOf("finance", "teacher", "parent", "student", "tab")
                ),
                FAQItem(
                    question = "Where do finance charges and transactions come from now?",
                    answer = "The finance screen is no longer using local placeholder students or sample charges. Charges, transactions, and linked student context now come from the backend and database-backed seed data.",
                    tags = listOf("finance", "database", "transactions", "charges", "backend")
                ),
                FAQItem(
                    question = "Why do I see payment logos in transactions?",
                    answer = "SchoolBridge can show supported payment providers such as MoMo, Equity, BK, Airtel, or Irembo in the transaction timeline so the payment method is easier to scan visually.",
                    tags = listOf("payment", "logos", "momo", "equity", "bk", "airtel", "irembo")
                ),
                FAQItem(
                    question = "Can I still submit a bank payment reference?",
                    answer = "Yes. A school can review your submitted payment proof or reference in the finance flow, and follow-up clarification can continue through the related communication thread.",
                    tags = listOf("bank", "reference", "receipt", "finance", "thread")
                )
            )
        ),
        FAQCategory(
            "Schedule & meetings", listOf(
                FAQItem(
                    question = "Why does the timetable include more than classes?",
                    answer = "SchoolBridge timetable is becoming a schedule surface, not only a class grid. It can combine lessons with planned calls, meetings, and announcement moments that come from communication threads.",
                    tags = listOf("timetable", "schedule", "calls", "meetings", "announcements")
                ),
                FAQItem(
                    question = "Do students in the same class share the same timetable?",
                    answer = "In high school, students in the same class usually share the same schedule. University flow can be different because course combinations and retakes may produce more individual timetables.",
                    tags = listOf("timetable", "class", "students", "high school", "university")
                ),
                FAQItem(
                    question = "Can a parent see meetings or planned calls in the timetable?",
                    answer = "Yes. When a parent has planned communication moments linked to a student or thread, those moments can appear alongside timetable events so the page feels like a real family schedule.",
                    tags = listOf("parent", "meeting", "planned call", "timetable", "schedule")
                ),
                FAQItem(
                    question = "Why does the selected day matter in weekly and flow views?",
                    answer = "The selected day helps SchoolBridge anchor the schedule you are exploring. It should stay meaningful across daily and weekly views, including when the layout changes between portrait and landscape.",
                    tags = listOf("selected day", "week", "flow", "timetable", "landscape")
                ),
                FAQItem(
                    question = "Can teachers get something other than finance in the bottom nav?",
                    answer = "Yes. If finance is not relevant to a role, SchoolBridge can prioritize more useful tools such as schedule, teaching desk, or other role-aware destinations.",
                    tags = listOf("teacher", "navigation", "schedule", "finance", "bottom nav")
                )
            )
        ),
        FAQCategory(
            "Profile, privacy & security", listOf(
                FAQItem(
                    question = "What is the QR code in my profile for?",
                    answer = "The QR is most useful as a SchoolBridge ID pass, for example school reception check-in, campus identity checks, pickup verification, or verified staff and student presence where the school supports it.",
                    tags = listOf("qr", "profile", "id pass", "verification")
                ),
                FAQItem(
                    question = "Why might I need to verify again on another phone or tablet?",
                    answer = "SchoolBridge may ask for verification again when you switch devices so the school account stays tied to the right person.",
                    tags = listOf("verify", "phone", "tablet", "device", "security")
                ),
                FAQItem(
                    question = "Can I use the same account on more than one device?",
                    answer = "Yes, as long as you sign in with the same SchoolBridge account. If both devices are online, live updates can keep message threads and actions in sync.",
                    tags = listOf("multiple devices", "login", "sync", "messages")
                ),
                FAQItem(
                    question = "Is my information shared publicly with other users?",
                    answer = "No. SchoolBridge is meant for school-related workflows. Schools, approved staff, and linked guardians only see the information needed for their responsibilities and approved relationships.",
                    tags = listOf("privacy", "data", "shared", "users", "security")
                ),
                FAQItem(
                    question = "Why does the app keep some setup values out of Git?",
                    answer = "Local machine paths, personal emulator references, and private API addresses should stay in private configuration files so the project remains portable for other developers without exposing personal workstation details.",
                    tags = listOf("privacy", "git", "config", "local properties", "developer")
                ),
                FAQItem(
                    question = "How are friendly error messages handled?",
                    answer = "SchoolBridge tries to turn technical server responses into clearer user guidance. For example, instead of showing a raw word like Forbidden, the app can explain what the user should try next.",
                    tags = listOf("errors", "friendly", "guidance", "forbidden", "network")
                ),
                FAQItem(
                    question = "Can I edit my profile details later?",
                    answer = "Yes. The profile area is meant to let you maintain current phone, address, and identity details so school workflows such as linking, verification, and contact remain accurate.",
                    tags = listOf("profile", "edit", "identity", "phone", "address")
                )
            )
        )
    )
}
